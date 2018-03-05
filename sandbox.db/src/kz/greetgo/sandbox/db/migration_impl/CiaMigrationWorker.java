package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

@Bean
public class CiaMigrationWorker extends AbstractMigrationWorker {

  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<IdGenerator> idGen;

  private String tmpClientTable;
  private ClientRecordsToSave clientRecord;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();
//    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    tmpClientTable = "cia_migration_sandbox_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    createPostgresConnection();
    dropTmpTables();
    createTmpTables();

    int recordsSize = download();

    {
      long now = System.nanoTime();
      info("Downloading of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

//    if (recordsSize == 0) return 0;

    handleErrors();

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("CiaMigrationWorker of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

    closePostgresConnection();

    return recordsSize;
  }


  protected void dropTmpTables() throws SQLException {
    exec("DROP TABLE IF EXISTS TMP_CLIENT, tmp_charm, tmp_addr, tmp_phone");
  }

  protected void handleErrors() throws SQLException {
    //language=PostgreSQL
    exec("UPDATE tmp_charm SET error = 'charm is not defined' " +
      "WHERE error IS NULL AND name IS NULL");


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
    exec("UPDATE TMP_CLIENT SET error = 'client too old or too young'\n" +
      "WHERE error IS NULL AND date_part('year', age(birth_date)) NOT BETWEEN 10 AND 200");


    uploadAndDropErrors();
  }

  public static void main(String[] args) throws IOException, SAXException, InterruptedException {

  }

  protected void uploadAndDropErrors() throws SQLException {

    // create report about errors and send by ssh

    //language=PostgreSQL
    exec("DELETE FROM tmp_charm WHERE error IS NOT NULL");

    //language=PostgreSQL
    exec("DELETE FROM TMP_CLIENT WHERE error IS NOT NULL");
  }

  protected void createTmpTables() throws SQLException {
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
    exec("CREATE TABLE tmp_charm (\n" +
//      "        cia_id VARCHAR(32),\n" +
      "        charm_id VARCHAR(32),\n" +
      "        name VARCHAR(255),\n" +
      "        description VARCHAR(255),\n" +
      "        energy REAL,\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE tmp_addr (\n" +
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
    exec("CREATE TABLE tmp_phone (\n" +
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

  protected long migrateFromTmp() throws Exception {
    //marking duplicates
    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, cia_id, row_number() OVER(PARTITION BY cia_id ORDER BY number DESC) AS ord \n" +
      "  FROM TMP_CLIENT\n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_CLIENT SET status = 2\n" +
      "WHERE status = 0 AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT t SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = t.cia_id\n");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET status = 3 WHERE client_id IS NOT NULL AND status = 0");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET client_id = nextval('s_client') WHERE status = 0");

    //inserting new charms
    //language=PostgreSQL
    exec("INSERT INTO charm (id, name)\n" +
      "SELECT nextval('s_client'), charm_name\n" +
      "FROM TMP_CLIENT tcl WHERE tcl.status = 0 ON CONFLICT (name) DO NOTHING");

    //language=PostgreSQL
    exec("INSERT INTO client (id, cia_id, surname, \"name\", patronymic, gender, birth_date, charm)\n" +
      "SELECT client_id, tcl.cia_id, surname, tcl.name, patronymic, gender, birth_date, ch.id\n" +
      "FROM TMP_CLIENT tcl LEFT JOIN charm ch ON tcl.charm_name = ch.name WHERE tcl.status = 0");

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
      "  FROM tmp_addr\n" +
      ")\n" +
      "\n" +
      "UPDATE tmp_addr SET status = 2\n" +
      "WHERE status = 0 AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE tmp_addr t SET client_id = ca.client\n" +
      "  FROM client_addr ca\n" +
      "  WHERE ca.cia_id = t.cia_id\n");

    //language=PostgreSQL
    exec("UPDATE tmp_addr SET status = 3 WHERE client_id IS NOT NULL AND status = 0");

    //language=PostgreSQL
    exec("UPDATE tmp_addr ta SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = ta.cia_id AND ta.status = 0\n");

    //
    //language=PostgreSQL
    exec("UPDATE tmp_addr SET status = 1 WHERE client_id IS NULL AND status = 0");

    //language=PostgreSQL
    exec("INSERT INTO client_addr (client, type, street, house, flat, cia_id)\n" +
      "SELECT client_id, type, street, house, flat, cia_id\n" +
      "FROM tmp_addr WHERE status = 0");

    //language=PostgreSQL
    exec("UPDATE client_addr ca SET type = ta.type\n" +
      "                 , street = ta.street\n" +
      "                 , house = ta.house\n" +
      "                 , flat = ta.flat\n" +
      "                 , cia_id = ta.cia_id\n" +
      "FROM tmp_addr ta\n" +
      "WHERE ca.client = ta.client_id\n" +
      "AND ta.status = 3");

    //language=PostgreSQL
    exec("UPDATE client_addr SET actual = 1 WHERE client IN (\n" +
      "  SELECT client_id FROM tmp_addr WHERE status = 0\n" +
      ")");


    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, cia_id, phone_number, row_number() OVER(PARTITION BY cia_id, phone_number ORDER BY number DESC) AS ord\n" +
      "  FROM tmp_phone\n" +
      ")\n" +
      "\n" +
      "UPDATE tmp_phone SET status = 2\n" +
      "WHERE status = 0 AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE tmp_phone tp SET client_id = cp.client\n" +
      "  FROM client_phone cp\n" +
      "  WHERE cp.cia_id = tp.cia_id\n");

    // marking what needs to be updated
    //language=PostgreSQL
    exec("UPDATE tmp_phone SET status = 3 WHERE client_id IS NOT NULL AND status = 0");

    // prepare to insertion
    //language=PostgreSQL
    exec("UPDATE tmp_phone ta SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = ta.cia_id AND ta.status = 0\n");

    //
    //language=PostgreSQL
    exec("UPDATE tmp_phone SET status = 1 WHERE client_id IS NULL AND status = 0");

    //language=PostgreSQL
    exec("INSERT INTO client_phone (client, number, type, cia_id)\n" +
      "SELECT client_id, phone_number, type, cia_id\n" +
      "FROM tmp_phone WHERE status = 0");

    //language=PostgreSQL
    exec("UPDATE client_phone cp SET number = tp.phone_number\n" +
      "                 , type = tp.type\n" +
      "                 , cia_id = tp.cia_id\n" +
      "FROM tmp_phone tp\n" +
      "WHERE cp.client = tp.client_id\n" +
      "AND tp.status = 3");

    //language=PostgreSQL
    exec("UPDATE client_phone SET actual = 1 WHERE client IN (\n" +
      "  SELECT client_id FROM tmp_phone WHERE status = 0\n" +
      ")");

    //send report by ssh

    return 0;
  }

  private void createPostgresConnection() throws Exception {
    connection = DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );
  }

  protected int download() throws Exception {

    // get file, read all files iteratively
    List<String> fileDirToLoad = renameFiles();

    for (String fileName : fileDirToLoad) {
      inputStream = new FileInputStream(fileName);
      TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(inputStream));
      TarArchiveEntry currentEntry = tarInput.getNextTarEntry();

      final AtomicBoolean working = new AtomicBoolean(true);
      final AtomicBoolean showStatus = new AtomicBoolean(false);

      final Thread see = new Thread(() -> {

        while (working.get()) {

          try {
            Thread.sleep(showStatusPingMillis);
          } catch (InterruptedException e) {
            break;
          }

          showStatus.set(true);

        }

      });
      see.start();

      int recordsCount;
      long startedAt = System.nanoTime();

      // parse xml and insert into tmp tables
      connection.setAutoCommit(false);

      try (TableWorker tableWorker = new TableWorker(connection, maxBatchSize)) {
        CiaParser ciaParser = new CiaParser(tarInput, tableWorker);
        recordsCount = ciaParser.parseAndSave();
      } finally {
        connection.setAutoCommit(true);
      }

      if (showStatus.get()) {
        showStatus.set(false);

        long now = System.nanoTime();
        info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
          + " : " + recordsPerSecond(recordsCount, now - startedAt));
      }

      {
        long now = System.nanoTime();
        info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
          + " : " + recordsPerSecond(recordsCount, now - startedAt));
      }
    }

    return 0;
  }

  protected List<String> renameFiles() throws IOException {
    List<String> ret = new ArrayList<>();
    String folderName = "build/files_to_send/";
    final File folder = new File(folderName);
    final String ext = ".xml.tar.bz2";
    List<String> fileNames = listFilesForFolder(folder);
    String regexPattern = "^[a-zA-Z0-9-_]*.xml.tar.bz2$";
    Pattern p = Pattern.compile(regexPattern);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    for (String fileName : fileNames) {
      if (p.matcher(fileName).matches()) {
        File file = new File(folderName + fileName);
        String newName = folderName + fileName.substring(0, fileName.length() - ext.length()) + "_YMD" + ext;
        ret.add(newName);
        File file1 = new File(newName);
        if (file1.exists())
          throw new java.io.IOException("file exists");
        boolean success = file.renameTo(file1);
        if (!success) {
          System.out.println("File was not successfully renamed");
          return null;
        }
      }
    }

    return ret;
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
