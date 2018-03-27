package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.InputFileWorker;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.ssh.SshConnection;
import kz.greetgo.sandbox.db.util.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Bean
public class Migration {
  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<MigrationConfig> migrationConfig;
  private boolean sshMode = true;


  public void executeMigration() throws Exception {

    File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
    outErrorFile.getParentFile().mkdirs();

    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream( file));
    reportXlsx.start();

    File fileFrs = new File(migrationConfig.get().sqlReportDir() + "sqlReportFrs.xlsx");
    fileFrs.getParentFile().mkdirs();
    ReportXlsx reportXlsxFrs = new ReportXlsx(new FileOutputStream( fileFrs));
    reportXlsxFrs.start();

    try (
      Connection connectionForCia = getConnection();
      Connection connectionForFrs = getConnection();
      InputFileWorker inputFileWorker = getInputFileWorker()
    ) {
      long startedAt = System.nanoTime();

      CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connectionForCia);
      ciaMigrationWorker.outErrorFile = outErrorFile;
      ciaMigrationWorker.reportXlsx = reportXlsx;
      ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();

      FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connectionForFrs);
//      frsMigrationWorker.outErrorFile = outErrorFile;
      frsMigrationWorker.reportXlsx = reportXlsxFrs;
      frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();

      final Thread frsDownloading = new Thread(() -> {
        try {
          frsMigrationWorker.createTmpTables();
          frsMigrationWorker.parseDataAndSaveInTmpDb();
          frsMigrationWorker.validateErrors();
        } catch (SQLException | SftpException | IOException e) {
          e.printStackTrace();
        }
      });
      frsDownloading.start();
      ciaMigrationWorker.migrate();
      frsDownloading.join();

      frsMigrationWorker.migrateFromTmp();

      long now = System.nanoTime();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
      System.out.println(sdf.format(new Date()) + " [FRS and CIA] TOTAL migration ended for time " + TimeUtils.showTime(now, startedAt) + " s");
    } finally {
      reportXlsx.finish();
      reportXlsxFrs.finish();
    }
  }


  public void executeCiaMigration() throws Exception {
    try (
      InputFileWorker inputFileWorker = getInputFileWorker();
      Connection connection = getConnection()
      ) {

      List<String> fileNamesToMigrate = inputFileWorker.getFileNamesToMigrate(".xml.tar.bz2");

      for (String fileName : fileNamesToMigrate) {
        File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + "error_report_" + fileName);
        //noinspection ResultOfMethodCallIgnored
        outErrorFile.getParentFile().mkdirs();
        File file = new File(migrationConfig.get().sqlReportDir() + fileName + "sqlReportCia.xlsx");
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream(file));
        reportXlsx.start();

        try (
          OutputStream outError = new FileOutputStream(outErrorFile)
        ) {
          CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection);
          ciaMigrationWorker.inputStream = inputFileWorker.downloadFile(fileName);
          ciaMigrationWorker.outError = outError;
          ciaMigrationWorker.reportXlsx = reportXlsx;
          ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
          ciaMigrationWorker.migrate();
        } finally {
          reportXlsx.finish();
        }
      }
    }
  }

  public void executeFrsMigration() throws Exception {



    File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
    outErrorFile.getParentFile().mkdirs();
    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportFrs.xlsx");
    file.getParentFile().mkdirs();
    ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();

    try (
      Connection connection = getConnection();
      InputFileWorker inputFileWorker = getInputFileWorker();
      OutputStream outError = new FileOutputStream(outErrorFile);
      ) {
      FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection);
      frsMigrationWorker.inputStream = inputFileWorker.downloadFile("");
      frsMigrationWorker.outError = outError;
      frsMigrationWorker.outErrorFile = outErrorFile;
      frsMigrationWorker.reportXlsx = reportXlsx;
      frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
      frsMigrationWorker.migrate();
    } finally {
      reportXlsx.finish();
    }
  }

  public InputFileWorker getInputFileWorker() throws JSchException {
    if (sshMode) {
      SshConnection sshConnection = new SshConnection(migrationConfig.get().inFilesHomePath());
      sshConnection.createSshConnection(migrationConfig.get().sshUser(),
        migrationConfig.get().sshPassword(),
        migrationConfig.get().sshHost(),
        migrationConfig.get().sshPort());
      return sshConnection;
    } else {
      return new LocalFileWorker(migrationConfig.get().inFilesHomePath());
    }
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );
  }

  public void setSshMode(boolean sshMode) {
    this.sshMode = sshMode;
  }
}
