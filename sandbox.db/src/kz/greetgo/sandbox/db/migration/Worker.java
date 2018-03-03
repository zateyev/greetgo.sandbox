package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.migration.Migration;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public abstract class Worker implements Migration {
  public InputStream inputStream;
  public OutputStream outputStream;
  public Connection connection;
  public int maxBatchSize = 50_000; // getConfig().maxBatchSize;

  public BeanGetter<JdbcSandbox> jdbcSandbox;

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<IdGenerator> idGen;

  private String tmpClientTable;
  public int portionSize = 1_000_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  @Override
  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();
//    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    tmpClientTable = "cia_migration_sandbox_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    createPostgresConnection();
    dropTmpTables();
    createTmpTables();

    int recordsSize = download();

    {
      long now = System.nanoTime();
      info("Downloading of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

//    if (recordsSize == 0) return 0;

    handleErrors();

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("MigrationWorker of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

    closePostgresConnection();

    return recordsSize;
  }


  abstract void dropTmpTables();

  abstract void handleErrors();

  abstract void uploadAndDropErrors();

  abstract void createTmpTables();

  abstract long migrateFromTmp();

  private void createPostgresConnection() throws Exception {
    connection = DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );
  }

  private String r(String sql) {
//    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_CLIENT", "tmp_client");
    sql = sql.replaceAll("NEW_ID", idGen.get().newId());
    return sql;
  }

  private void exec(String sql) throws SQLException {
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

  private List<String> listFilesForFolder(final File folder) {
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

  abstract int download();

  abstract void insertIntoTmpTables(List<ClientRecordsToSave> clientRecords);

  abstract List<String> renameFiles();

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  private void closePostgresConnection() {
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
