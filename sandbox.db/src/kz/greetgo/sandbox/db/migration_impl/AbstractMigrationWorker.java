package kz.greetgo.sandbox.db.migration_impl;

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
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public abstract class AbstractMigrationWorker /*implements CiaMigrationWorker */{
  public InputStream inputStream;
  public OutputStream outputStream;
  public Connection connection;
  public int maxBatchSize = 50_000; // getConfig().maxBatchSize;

  private String tmpClientTable;
  public int portionSize = 1_000_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  protected abstract void dropTmpTables() throws SQLException;

  protected abstract void handleErrors() throws SQLException, IOException;

  protected abstract void uploadAndDropErrors() throws SQLException, IOException;

  protected abstract void createTmpTables() throws SQLException;

  protected abstract long migrateFromTmp() throws Exception;

  protected abstract int download() throws Exception;

  protected abstract String r(String sql);

  protected List<String> renameFiles(String ext) throws IOException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();

    List<String> ret = new ArrayList<>();
    String folderName = "build/files_to_send/";
    final File folder = new File(folderName);
    List<String> fileNames = listFilesForFolder(folder);
    String regexPattern = "^[a-zA-Z0-9-_]*" + ext + "$";
    Pattern p = Pattern.compile(regexPattern);

    for (String fileName : fileNames) {
      if (p.matcher(fileName).matches()) {
        File file = new File(folderName + fileName);
        String newName = folderName + fileName.substring(0, fileName.length() - ext.length()) +
          "_sandbox" + sdf.format(nowDate) + ext;
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

  protected void info(String message) {
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
