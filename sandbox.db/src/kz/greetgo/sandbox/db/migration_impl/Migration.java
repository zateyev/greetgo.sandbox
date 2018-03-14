package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.JSchException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
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


  public void executeCiaMigration() throws Exception {
    Connection connection = getConnection();
    SshConnection sshConnection = getSshConnection();

    File outErrorFile = new File(migrationConfig.get().outErrorFileName());
    ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream( migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx"));
    reportXlsx.start();

    try {
      CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection, sshConnection);
      ciaMigrationWorker.outErrorFile = outErrorFile;
      ciaMigrationWorker.reportXlsx = reportXlsx;
      ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
      ciaMigrationWorker.migrate();
    } finally {
      reportXlsx.finish();
      if (connection != null) {
        connection.close();
      }
      if (sshConnection != null) {
        sshConnection.close();
      }
    }
  }

  public void executeFrsMigration() throws Exception {
    Connection connection = getConnection();
    SshConnection sshConnection = getSshConnection();

    File outErrorFile = new File(migrationConfig.get().outErrorFileName());
    ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream(migrationConfig.get().sqlReportDir() + "sqlReportFrs.xlsx"));
    reportXlsx.start();

    try {
      FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection, sshConnection);
      frsMigrationWorker.outErrorFile = outErrorFile;
      frsMigrationWorker.reportXlsx = reportXlsx;
      frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
      frsMigrationWorker.migrate();
    } finally {
      if (connection != null) {
        connection.close();
      }
      if (sshConnection != null) {
        sshConnection.close();
      }
    }
  }

  private SshConnection getSshConnection() throws JSchException {
    SshConnection sshConnection = new SshConnection(migrationConfig.get().sshHomePath());
    sshConnection.createSshConnection(migrationConfig.get().sshUser(),
      migrationConfig.get().sshPassword(),
      migrationConfig.get().sshHost(),
      migrationConfig.get().sshPort());
    return sshConnection;
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );
  }
}
