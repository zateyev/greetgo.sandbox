package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.SshConnection;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

@Bean
public class FrsMigrationWorkerImpl extends AbstractMigrationWorker {

  private String tmpAccountTable;
  private String tmpTransactionTable;

  @Override
  protected void dropTmpTables() throws SQLException {
    exec("DROP TABLE IF EXISTS TMP_ACCOUNT, TMP_TRANSACTION");
  }

  @Override
  protected void handleErrors() {

  }

  @Override
  protected void uploadErrors() {

  }

  @Override
  protected String r(String sql) {
    sql = sql.replaceAll("TMP_ACCOUNT", tmpAccountTable);
    sql = sql.replaceAll("TMP_TRANSACTION", tmpTransactionTable);
    return sql;
  }

  @Override
  protected void createTmpTables() throws SQLException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpAccountTable = "cia_migration_account_" + sdf.format(nowDate);
    tmpTransactionTable = "cia_migration_transaction_" + sdf.format(nowDate);

    //language=PostgreSQL
    exec("CREATE TABLE TMP_ACCOUNT (\n" +
      "  type           VARCHAR(32),\n" +
      "  client_id      VARCHAR(32),\n" +
      "  account_number VARCHAR(64),\n" +
      "  registered_at  TIMESTAMP WITH TIME ZONE,\n" +
      "\n" +
      "  status         INT NOT NULL DEFAULT 0,\n" +
      "  error          VARCHAR(255),\n" +
      "  number         BIGSERIAL PRIMARY KEY\n" +
      ")");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_TRANSACTION (\n" +
      "  type             VARCHAR(32),\n" +
      "  money            DECIMAL,\n" +
      "  finished_at      TIMESTAMP WITH TIME ZONE,\n" +
      "  transaction_type VARCHAR(255),\n" +
      "  account_number   VARCHAR(100),\n" +
      "\n" +
      "  status           INT NOT NULL DEFAULT 0,\n" +
      "  error            VARCHAR(255),\n" +
      "  number           BIGSERIAL PRIMARY KEY\n" +
      ")");
  }

  @Override
  protected void migrateFromTmp() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client (id, cia_id)\n" +
      "SELECT nextval('s_client'), client_id\n" +
      "FROM TMP_ACCOUNT ta ON CONFLICT (cia_id) DO NOTHING");

    //language=PostgreSQL
    exec("INSERT INTO client_account (id, client, money, number, registered_at)\n" +
      "SELECT nextval('s_client'), c.id, tt.money, ta.account_number, ta.registered_at\n" +
      "FROM TMP_ACCOUNT ta LEFT JOIN (SELECT account_number, sum(money) money FROM TMP_TRANSACTION GROUP BY account_number) tt\n" +
      "ON tt.account_number = ta.account_number LEFT JOIN client c ON c.cia_id = ta.client_id");

    //language=PostgreSQL
    exec("INSERT INTO client_account_transaction (id, account, money, finished_at, type)\n" +
      "SELECT nextval('s_client'), ca.id, tt.money, tt.finished_at, tt.transaction_type\n" +
      "FROM TMP_TRANSACTION tt LEFT JOIN client_account ca ON tt.account_number = ca.number");


  }

  @Override
  protected int download() throws IOException, SQLException, SftpException {
    List<String> fileDirToLoad = renameFiles(".json_row.txt.tar.bz2");
    int recordsCount = 0;
    long downloadingStartedAt = System.nanoTime();

    for (String fileName : fileDirToLoad) {
      inputStream = sshConnection.download(fileName);
      TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(inputStream));
      TarArchiveEntry currentEntry = tarInput.getNextTarEntry();

      long startedAt = System.nanoTime();

      maxBatchSize = migrationConfig.get().maxBatchSize();
      connection.setAutoCommit(false);

      try (FrsTableWorker frsTableWorker = new FrsTableWorker(connection, maxBatchSize, tmpAccountTable, tmpTransactionTable)) {
        FrsParser frsParser = new FrsParser(tarInput, frsTableWorker);
        recordsCount += frsParser.parseAndSave();
      } finally {
        connection.setAutoCommit(true);
      }

      {
        long now = System.nanoTime();
        info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
          + " : " + recordsPerSecond(recordsCount, now - startedAt));
      }
    }

    {
      long now = System.nanoTime();
      info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, downloadingStartedAt)
        + " : " + recordsPerSecond(recordsCount, now - downloadingStartedAt));
    }

    return recordsCount;
  }

  @Override
  protected void createConnections() throws Exception {
    try {
      reportXlsx = new ReportXlsx(new FileOutputStream(migrationConfig.get().sqlReportDir() + "sqlReportFrs.xlsx"));
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
}
