package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.JSchException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.InputFileWorker;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.ssh.SshConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Bean
public class Migration {
  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<MigrationConfig> migrationConfig;
  private boolean sshMode = true;


  public void executeCiaMigration() throws Exception {

    File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
    outErrorFile.getParentFile().mkdirs();
    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream( file));
    reportXlsx.start();

    try (
      Connection connection = getConnection();
      InputFileWorker inputFileWorker = getInputFileWorker()
      ) {
      CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection, inputFileWorker);
      ciaMigrationWorker.outErrorFile = outErrorFile;
      ciaMigrationWorker.reportXlsx = reportXlsx;
      ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
      ciaMigrationWorker.migrate();
    } finally {
      reportXlsx.finish();
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
      InputFileWorker sshConnection = getInputFileWorker();
      ) {
      FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection, sshConnection);
      frsMigrationWorker.outErrorFile = outErrorFile;
      frsMigrationWorker.reportXlsx = reportXlsx;
      frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
      frsMigrationWorker.migrate();
    } finally {
      reportXlsx.finish();
    }
  }

  private InputFileWorker getInputFileWorker() throws JSchException {
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

  private Connection getConnection() throws SQLException {
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
