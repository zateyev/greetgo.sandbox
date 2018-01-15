package kz.greetgo.sandbox.db.register_impl.migration;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class MigrationFrs {

  public File inFile, errorsFile;
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
  String errorLog = new String();

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
        " registered_at varchar(100)," +
        " status varchar(100) default 'JUST_INSERTED'," +
        " error varchar(100) default null" +
        ")"
    );

    exec(
      "create table " + transactionTable + "(" +
        " money varchar(100)," +
        " finished_at varchar(100)," +
        " transaction_type varchar(300)," +
        " account_number varchar(50)," +
        " status varchar(100) default 'JUST_INSERTED'," +
        " error varchar(100) default null" +
        ")"
    );

  }

  void uploadFileToTempTables() throws Exception {

    FrsParser handler = new FrsParser(
      connection,
      maxBatchSize,
      accountTable,
      transactionTable
    );

    try (Stream<String> lines = Files.lines(inFile.toPath())) {
      for (String line : (Iterable<String>) lines::iterator)
      {
        handler.parseAndAddBatch(line);
      }

      handler.close();
    }//TODO napisat' catch na JsonParseException v file errorsFile

  }

  void mainMigrationOperation() {}

  void downloadErrors() throws IOException {

    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

    errorsFile = new File("build/errorsFile_" + date + ".log");
    errorsFile.getParentFile().mkdirs();

    FileWriter out = new FileWriter(errorsFile);
    out.write("FileName: " + inFile + "\n");
    out.write(errorLog.toString());
    out.close();

  }


}
