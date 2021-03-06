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
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Bean
public class Migration {
  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<MigrationConfig> migrationConfig;
  private boolean sshMode = true;

  public void executeCiaMigration() throws Exception {
    try (
      InputFileWorker inputFileWorker = getInputFileWorker();
      Connection connection = getConnection()
      ) {

      List<String> fileNamesToMigrate = inputFileWorker.getFileNamesToMigrate(".xml.tar.bz2");

      for (String fileName : fileNamesToMigrate) {
        File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + "error_report_" + fileName);
        outErrorFile.getParentFile().mkdirs();
        File file = new File(migrationConfig.get().sqlReportDir() + fileName + "sqlReportCia.xlsx");
        file.getParentFile().mkdirs();
        ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream(file));
        reportXlsx.start();

        try (
          OutputStream outError = new FileOutputStream(outErrorFile)
        ) {
          CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection);

          ciaMigrationWorker.inputStream = extractFromFile(inputFileWorker.downloadFile(fileName));
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

    try (
      InputFileWorker inputFileWorker = getInputFileWorker();
      Connection connection = getConnection()
      ) {

      List<String> fileNamesToMigrate = inputFileWorker.getFileNamesToMigrate(".json_row.txt.tar.bz2");

      for (String fileName : fileNamesToMigrate) {
        File outErrorFile = new File(migrationConfig.get().inFilesHomePath() + "error_report_" + fileName);
        outErrorFile.getParentFile().mkdirs();
        File file = new File(migrationConfig.get().sqlReportDir() + fileName + "sqlReportFrs.xlsx");
        file.getParentFile().mkdirs();
        ReportXlsx reportXlsx = new ReportXlsx(new FileOutputStream(file));
        reportXlsx.start();

        try (
          OutputStream outError = new FileOutputStream(outErrorFile);
        ) {
          FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection);

          frsMigrationWorker.inputStream = extractFromFile(inputFileWorker.downloadFile(fileName));
          frsMigrationWorker.outError = outError;
          frsMigrationWorker.outErrorFile = outErrorFile;
          frsMigrationWorker.reportXlsx = reportXlsx;
          frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
          frsMigrationWorker.migrate();
        } finally {
          reportXlsx.finish();
        }
      }
    }
  }

  private InputStream extractFromFile(InputStream inputStream) throws IOException {
    BZip2CompressorInputStream bZip2CompressorIS = new BZip2CompressorInputStream(inputStream);
    TarArchiveInputStream tarInput = new TarArchiveInputStream(bZip2CompressorIS);
    tarInput.getNextTarEntry();
    return tarInput;
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
