package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.migration.MigrationWorker;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.SshConnection;
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

public abstract class AbstractMigrationWorker implements MigrationWorker, AutoCloseable {

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<MigrationConfig> migrationConfig;

  public InputStream inputStream;
  public OutputStream outError;
  private ReportXlsx reportXlsx;
  public Connection connection;
  public int maxBatchSize;
  protected SshConnection sshConnection;

  protected AbstractMigrationWorker() {
    try {
      reportXlsx = new ReportXlsx(new FileOutputStream("build/files_to_send/report.xlsx"));
      reportXlsx.start();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    createConnections();

    createTmpTables();

    int recordsSize = download();

    handleErrors();

    migrateFromTmp();

    String message;
    {
      long now = System.nanoTime();
      message = "Migration of portion " + recordsSize + " finished for " + showTime(now, startedAt);
      info(message);
    }

    closePostgresConnection();
    close();

    return recordsSize;
  }

  protected abstract void createConnections() throws Exception;

  protected abstract void dropTmpTables() throws SQLException;

  protected abstract void handleErrors() throws SQLException, IOException;

  protected abstract void uploadErrors() throws SQLException, IOException;

  protected abstract void createTmpTables() throws SQLException;

  protected abstract void migrateFromTmp() throws Exception;

  protected abstract int download() throws Exception;

  protected abstract String r(String sql);

  protected List<String> renameFiles(String ext) throws IOException, SftpException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();

    List<String> ret = new ArrayList<>();
    List<String> fileNames = sshConnection.getFileNames(ext);
    String regexPattern = "^[a-zA-Z0-9-_]*" + ext + "$";
    Pattern p = Pattern.compile(regexPattern);
    String processId = RND.intStr(5);

    for (String fileName : fileNames) {
      if (p.matcher(fileName).matches()) {
        String newName = fileName + "." + processId + "_" + sdf.format(nowDate);
        sshConnection.renameFile(fileName, newName);
        ret.add(newName);
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

//  protected List<String> listFilesForFolder(final File folder) {
//    List<String> ret = new ArrayList<>();
//    for (final File fileEntry : folder.listFiles()) {
//      if (fileEntry.isDirectory()) {
//        listFilesForFolder(fileEntry);
//      } else {
//        ret.add(fileEntry.getName());
//      }
//    }
//    return ret;
//  }

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
    sshConnection.close();
    reportXlsx.finish();
  }
}
