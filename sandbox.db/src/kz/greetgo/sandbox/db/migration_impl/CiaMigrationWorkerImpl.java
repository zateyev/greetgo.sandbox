package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.SshConnection;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

@Bean
public class CiaMigrationWorkerImpl extends AbstractMigrationWorker {

  private String tmpClientTable;
  private String tmpAddrTable;
  private String tmpPhoneTable;

  @Override
  protected String r(String sql) {
    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_ADDR", tmpAddrTable);
    sql = sql.replaceAll("TMP_PHONE", tmpPhoneTable);
    return sql;
  }

  protected void dropTmpTables() throws SQLException {
    //language=PostgreSQL
    exec("DROP TABLE IF EXISTS TMP_CLIENT, TMP_ADDR, TMP_PHONE");
  }

  protected void handleErrors() throws SQLException, IOException, SftpException {
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'surname is not defined' " +
      "WHERE error IS NULL AND (surname <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'name is not defined'\n" +
      "WHERE error IS NULL AND (name <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'birth_date is not defined'\n" +
      "WHERE error IS NULL AND birth_date IS NULL");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'charm is not defined'\n" +
      "WHERE error IS NULL AND charm_name IS NULL");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'age of the client must be between 10 and 200'\n" +
      "WHERE error IS NULL AND date_part('year', age(birth_date)) NOT BETWEEN 10 AND 200");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET status = 4 WHERE error IS NOT NULL");


    uploadErrors();
  }

  protected void uploadErrors() throws SQLException, IOException, SftpException {

    File errFile = new File(migrationConfig.get().outErrorFile());
    outError = new FileOutputStream(errFile);

    // create report about errors and send by ssh
    try (PreparedStatement errorPs = connection.prepareStatement(r("SELECT cia_id, error FROM TMP_CLIENT WHERE error IS NOT NULL"))) {
      try (ResultSet errorRs = errorPs.executeQuery()) {
        while (errorRs.next()) {
          outError.write("Error: ".getBytes());
          outError.write(errorRs.getBytes("error"));
          outError.write(". Record id: ".getBytes());
          outError.write(errorRs.getBytes("cia_id"));
          outError.write("\n".getBytes());
        }
      }
    } finally {
      outError.close();
      sshConnection.upload(errFile);
    }

    //language=PostgreSQL
//    exec("DELETE FROM TMP_CLIENT WHERE error IS NOT NULL");
  }

  protected void createTmpTables() throws SQLException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    tmpAddrTable = "cia_migration_addr_" + sdf.format(nowDate);
    tmpPhoneTable = "cia_migration_phone_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);
    info("TMP_ADDR = " + tmpAddrTable);
    info("TMP_PHONE = " + tmpPhoneTable);

    //language=PostgreSQL
    exec("CREATE TABLE TMP_CLIENT (\n" +
      "        cia_id VARCHAR(32),\n" +
      "        client_id VARCHAR(32),\n" +
      "        name VARCHAR(255),\n" +
      "        surname VARCHAR(255),\n" +
      "        patronymic VARCHAR(255),\n" +
      "        gender VARCHAR(12),\n" +
      "        birth_date DATE,\n" +
      "        charm_name VARCHAR(32),\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_ADDR (\n" +
      "        cia_id VARCHAR(32),\n" +
      "        client_id VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        street VARCHAR(255),\n" +
      "        house VARCHAR(32),\n" +
      "        flat VARCHAR(32),\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_PHONE (\n" +
      "        cia_id VARCHAR(32),\n" +
      "        client_id VARCHAR(32),\n" +
      "        phone_number VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");
  }

  protected void migrateFromTmp() throws Exception {
    //marking duplicates
    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, cia_id, row_number() OVER(PARTITION BY cia_id ORDER BY number DESC) AS ord \n" +
      "  FROM TMP_CLIENT WHERE error ISNULL\n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_CLIENT SET status = 2\n" +
      "WHERE status = 0 AND error ISNULL AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT t SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = t.cia_id AND t.error ISNULL \n");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET status = 3 WHERE client_id IS NOT NULL AND status = 0 AND error ISNULL ");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET client_id = nextval('s_client') WHERE status = 0 AND error ISNULL ");

    //inserting new charms
    //language=PostgreSQL
    exec("INSERT INTO charm (id, name)\n" +
      "SELECT nextval('s_client'), charm_name\n" +
      "FROM TMP_CLIENT tcl WHERE tcl.status = 0 AND tcl.error ISNULL ON CONFLICT (name) DO NOTHING");

    //language=PostgreSQL
    exec("INSERT INTO client (id, cia_id, surname, \"name\", patronymic, gender, birth_date, charm)\n" +
      "SELECT client_id, tcl.cia_id, surname, tcl.name, patronymic, gender, birth_date, ch.id\n" +
      "FROM TMP_CLIENT tcl LEFT JOIN charm ch ON tcl.charm_name = ch.name WHERE tcl.status = 0 AND tcl.error ISNULL");

    //language=PostgreSQL
    exec("UPDATE client c SET cia_id = tc.cia_id\n" +
      "                 , surname = tc.surname\n" +
      "                 , \"name\" = tc.\"name\"\n" +
      "                 , patronymic = tc.patronymic\n" +
      "                 , gender = tc.gender\n" +
      "                 , birth_date = tc.birth_date\n" +
      "                 , charm = ch.id\n" +
      "FROM TMP_CLIENT tc LEFT JOIN charm ch ON tc.charm_name = ch.name\n" +
      "WHERE c.id = tc.client_id\n" +
      "AND tc.status = 3");

    //language=PostgreSQL
    exec("UPDATE client SET actual = 1 WHERE id IN (\n" +
      "  SELECT client_id FROM TMP_CLIENT WHERE status = 0\n" +
      ")");


    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, cia_id, type, row_number() OVER(PARTITION BY cia_id, type ORDER BY number DESC) AS ord\n" +
      "  FROM TMP_ADDR WHERE error ISNULL \n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_ADDR SET status = 2\n" +
      "WHERE status = 0 AND error ISNULL AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE TMP_ADDR t SET client_id = ca.client\n" +
      "  FROM client_addr ca\n" +
      "  WHERE ca.cia_id = t.cia_id AND t.error ISNULL \n");

    //language=PostgreSQL
    exec("UPDATE TMP_ADDR SET status = 3 WHERE client_id IS NOT NULL AND status = 0 AND error ISNULL");

    //language=PostgreSQL
    exec("UPDATE TMP_ADDR ta SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = ta.cia_id AND ta.status = 0 AND ta.error ISNULL \n");

    //
    //language=PostgreSQL
    exec("UPDATE TMP_ADDR SET status = 1 WHERE client_id IS NULL AND status = 0 AND error ISNULL");

    //language=PostgreSQL
    exec("INSERT INTO CLIENT_ADDR (client, type, street, house, flat, cia_id)\n" +
      "SELECT client_id, type, street, house, flat, cia_id\n" +
      "FROM TMP_ADDR WHERE status = 0 AND error ISNULL");

    //language=PostgreSQL
    exec("UPDATE client_addr ca SET type = ta.type\n" +
      "                 , street = ta.street\n" +
      "                 , house = ta.house\n" +
      "                 , flat = ta.flat\n" +
      "                 , cia_id = ta.cia_id\n" +
      "FROM TMP_ADDR ta\n" +
      "WHERE ca.client = ta.client_id\n" +
      "AND ta.status = 3");

    //language=PostgreSQL
    exec("UPDATE client_addr SET actual = 1 WHERE client IN (\n" +
      "  SELECT client_id FROM TMP_ADDR WHERE status = 0 AND error ISNULL\n" +
      ")");


    //language=PostgreSQL
    exec("UPDATE TMP_PHONE tp SET status = tc.status FROM TMP_CLIENT tc WHERE \n" +
      " tp.cia_id = cast(tc.number AS VARCHAR(32))");

    //language=PostgreSQL
    exec("UPDATE TMP_PHONE tp SET client_id = tc.client_id FROM TMP_CLIENT tc WHERE \n" +
      " tp.cia_id = cast(tc.number AS VARCHAR(32)) AND tc.status <> 4 AND tc.status <> 2");

    //language=PostgreSQL
    exec("INSERT INTO client_phone (client, number, type, cia_id)\n" +
      "SELECT client_id, phone_number, type, cia_id\n" +
      "FROM TMP_PHONE WHERE status = 0");

    //language=PostgreSQL
    exec("UPDATE client_phone cp SET number = tp.phone_number\n" +
      "                 , type = tp.type\n" +
      "                 , cia_id = tp.cia_id\n" +
      "FROM TMP_PHONE tp\n" +
      "WHERE cp.client = tp.client_id\n" +
      "AND tp.status = 3");

    //language=PostgreSQL
    exec("UPDATE client_phone SET actual = 1 WHERE client IN (\n" +
      "  SELECT client_id FROM TMP_PHONE WHERE status = 0\n" +
      ")");
  }

  public static void main(String[] args) {
    Set<String> set1 = new HashSet<>();
    Set<String> set2 = new HashSet<>();
    set1.add("asd7asd");
    set1.add("asdTasd");
    set1.add("asdRasd");
    set1.add("asd3asd");

    set2.add("asd3asd");
    set2.add("asdTasd");
    set2.add("asdRasd");
    StringBuilder sb = new StringBuilder();
    sb.append("asd").append(7).append("asd");
    set2.add(sb.toString());

    System.out.println(Objects.equals(set1, set2));
  }

  @Override
  protected void createConnections() throws Exception {
    try {
      reportXlsx = new ReportXlsx(new FileOutputStream(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx"));
      reportXlsx.start();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    sshConnection = new SshConnection(migrationConfig.get().sshHomePath());
    sshConnection.createSshConnection(migrationConfig.get().sshUser(),
      migrationConfig.get().sshPassword(),
      migrationConfig.get().sshHost(),
      migrationConfig.get().sshPort());

    connection = DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );
  }

  protected int download() throws Exception {

    // get file, read all files iteratively
    List<String> fileDirToLoad = renameFiles(".xml.tar.bz2");
    int recordsCount = 0;
    long downloadingStartedAt = System.nanoTime();

    for (String fileName : fileDirToLoad) {
      inputStream = sshConnection.download(fileName);
      TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(inputStream));
      TarArchiveEntry currentEntry = tarInput.getNextTarEntry();

      long startedAt = System.nanoTime();

      maxBatchSize = migrationConfig.get().maxBatchSize();
      connection.setAutoCommit(false);

      try (CiaTableWorker ciaTableWorker = new CiaTableWorker(connection, maxBatchSize, tmpClientTable, tmpAddrTable, tmpPhoneTable)) {
        CiaParser ciaParser = new CiaParser(tarInput, ciaTableWorker, recordsCount);
        recordsCount = ciaParser.parseAndSave();
      } finally {
        connection.setAutoCommit(true);
      }

      {
        long now = System.nanoTime();
        info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
          + " : " + recordsPerSecond(recordsCount, now - startedAt));
      }

      inputStream.close();

    }

    {
      long now = System.nanoTime();
      info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, downloadingStartedAt)
        + " : " + recordsPerSecond(recordsCount, now - downloadingStartedAt));
    }

    return recordsCount;
  }
}
