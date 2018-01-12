package kz.greetgo.sandbox.db.register_impl.migration;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class MigrationFrs {

  public File inFile, errorFile;
  public Connection connection;
  public int maxBatchSize = 5000;

  private final Logger logger = Logger.getLogger(getClass());

  private void exec(String sql) throws SQLException {

    try (Statement statement = connection.createStatement()) {
      long startedAt = System.nanoTime();
      statement.execute(sql);
      logger.trace("SQL [" + (System.nanoTime() - startedAt) + "] " + sql);
    }
  }

  String accountTable, transactionTable;

  public void migrate() throws Exception {
    createTempTables();
    uploadFileToTempTables();
    mainMigrationOperation();
    downloadErrors();
  }

  void createTempTables() throws SQLException {

    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
    accountTable = "tmp_accounts_" + date;
    transactionTable = "tmp_transactions_" + date;

    exec(
      "create table " + accountTable + "(" +
        " cia_id varchar(50)," +
        " account_number varchar(50)," +
        " registered_at timestamp," +
        " status varchar(100) default 'JUST_INSERTED'," +
        " error varchar(100) default null" +
        ")"
    );

    exec(
      "create table " + transactionTable + "(" +
        " money varchar(100)," +
        " finished_at timestamp," +
        " transaction_type varchar(300)," +
        " account_number varchar(50)," +
        " status varchar(100) default 'JUST_INSERTED'," +
        " error varchar(100) default null" +
        ")"
    );

  }

  void uploadFileToTempTables() {

  }

  void mainMigrationOperation() {}

  void downloadErrors() {}


}
