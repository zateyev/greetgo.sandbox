package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.util.RND;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public abstract class AbstractMigrationWorker implements AutoCloseable {

  public BeanGetter<MigrationConfig> migrationConfig;

  public InputStream inputStream;
  public OutputStream outError;
  private ReportXlsx reportXlsx;
  public Connection connection;
  public int maxBatchSize;

  public int showStatusPingMillis = 5000;

  protected AbstractMigrationWorker() {
    try {
      reportXlsx = new ReportXlsx(new FileOutputStream("build/files_to_send/report.xlsx"));
      reportXlsx.start();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  protected abstract void dropTmpTables() throws SQLException;

  protected abstract void handleErrors() throws SQLException, IOException;

  protected abstract void uploadAndDropErrors() throws SQLException, IOException;

  protected abstract void createTmpTables() throws SQLException;

  protected abstract void migrateFromTmp() throws Exception;

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
          "_" + RND.str(8) + sdf.format(nowDate) + ext;
        ret.add(newName);
        File file1 = new File(newName);
        if (file1.exists())
          throw new java.io.IOException("file exists");
        boolean success = file.renameTo(file1);
        if (!success) {
          throw new RuntimeException("File was not successfully renamed");
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
      reportXlsx.addRow(executingSql, showTime(System.nanoTime(), startedAt));
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

  @Override
  public void close() throws Exception {
    reportXlsx.finish();
  }
}
