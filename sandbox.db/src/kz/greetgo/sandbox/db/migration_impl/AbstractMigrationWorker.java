package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.migration.MigrationWorker;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public abstract class AbstractMigrationWorker implements MigrationWorker {
  public InputStream inputStream;
  public OutputStream outputStream;
  public Connection connection;
  public int maxBatchSize = 50_000; // getConfig().maxBatchSize;

  private String tmpClientTable;
  public int portionSize = 1_000_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  protected abstract void dropTmpTables() throws SQLException;

  protected abstract void handleErrors() throws SQLException;

  protected abstract void uploadAndDropErrors() throws SQLException;

  protected abstract void createTmpTables() throws SQLException;

  protected abstract long migrateFromTmp() throws Exception;

  protected abstract int download() throws IOException, SAXException, SQLException;

  protected abstract void insertIntoTmpTables(List<ClientRecordsToSave> clientRecords) throws SQLException, IOException;

  protected abstract List<String> renameFiles() throws IOException;

  private String r(String sql) {
//    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_CLIENT", "tmp_client");
    return sql;
  }

  protected void exec(String sql) throws SQLException {
    String executingSql = r(sql);

    long startedAt = System.nanoTime();
    try (Statement statement = connection.createStatement()) {
      int updates = statement.executeUpdate(executingSql);
      info("Updated " + updates
        + " records for " + showTime(System.nanoTime(), startedAt)
        + ", EXECUTED SQL : " + executingSql);
    } catch (SQLException e) {
      info("ERROR EXECUTE SQL for " + showTime(System.nanoTime(), startedAt)
        + ", message: " + e.getMessage() + ", SQL : " + executingSql);
      throw e;
    }
  }

  protected List<String> listFilesForFolder(final File folder) {
    List<String> ret = new ArrayList<>();
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        listFilesForFolder(fileEntry);
      } else {
        System.out.println(fileEntry.getName());
        ret.add(fileEntry.getName());
      }
    }
    return ret;
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  protected void closePostgresConnection() {
    if (this.connection != null) {
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.connection = null;
    }
  }
}
