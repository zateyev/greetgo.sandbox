package kz.greetgo.sandbox.db.migration;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.PhoneNumber;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.util.ClientRecordParser;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

@Bean
public class MigrationWorker {

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

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
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


  private void dropTmpTables() throws SQLException {
    exec("DROP TABLE IF EXISTS TMP_CLIENT, tmp_charm, tmp_addr, tmp_phone");
  }

  private void handleErrors() throws SQLException {
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'surname is not defined' " +
      "WHERE error IS NULL AND surname IS NULL");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'name is not defined'\n" +
      "WHERE error IS NULL AND name IS NULL");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'birth_date is not defined'\n" +
      "WHERE error IS NULL AND birth_date IS NULL");

    uploadAndDropErrors();
  }

  private void uploadAndDropErrors() throws SQLException {

    // create report about errors and send by ssh

    //language=PostgreSQL
    exec("DELETE FROM TMP_CLIENT WHERE error IS NOT NULL");
  }

  private void createTmpTables() throws SQLException {
    //language=PostgreSQL
    exec("CREATE TABLE TMP_CLIENT (\n" +
      "        id VARCHAR(32),\n" +
      "        client_id VARCHAR(32),\n" +
      "        name VARCHAR(255),\n" +
      "        surname VARCHAR(255),\n" +
      "        patronymic VARCHAR(255),\n" +
      "        gender VARCHAR(12),\n" +
      "        birth_date DATE,\n" +
      "        charm VARCHAR(32),\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE tmp_charm (\n" +
      "        id VARCHAR(32) NOT NULL PRIMARY KEY,\n" +
      "        charm_id VARCHAR(32),\n" +
      "        name VARCHAR(255),\n" +
      "        description VARCHAR(255),\n" +
      "        energy REAL,\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE tmp_addr (\n" +
      "        client VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        street VARCHAR(255),\n" +
      "        house VARCHAR(32),\n" +
      "        flat VARCHAR(32),\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        PRIMARY KEY (client, type)\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE tmp_phone (\n" +
      "        client VARCHAR(32),\n" +
      "        number VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        PRIMARY KEY (client, number)\n" +
      "      )");
  }

  private long migrateFromTmp() throws Exception {

    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, \"name\", row_number() OVER(PARTITION BY \"name\" ORDER BY number DESC) AS ord \n" +
      "  FROM tmp_charm\n" +
      ")\n" +
      "\n" +
      "UPDATE tmp_charm SET status = 2\n" +
      "WHERE status = 0 AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE tmp_charm t SET charm_id = c.id\n" +
      "  FROM charm c\n" +
      "  WHERE c.id = t.id\n");

    //language=PostgreSQL
    exec("UPDATE tmp_charm SET status = 3 WHERE charm_id IS NOT NULL AND status = 0");

    //language=PostgreSQL
    exec("UPDATE tmp_charm SET charm_id = id WHERE status = 0");






    //marking duplicates
    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, id, row_number() OVER(PARTITION BY id ORDER BY number DESC) AS ord \n" +
      "  FROM TMP_CLIENT\n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_CLIENT SET status = 2\n" +
      "WHERE status = 0 AND number IN (SELECT number FROM num_ord WHERE ord > 1)");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT t SET client_id = c.id\n" +
      "  FROM client c\n" +
      "  WHERE c.id = t.id\n");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET status = 3 WHERE client_id IS NOT NULL AND status = 0");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET client_id = id WHERE status = 0");

//    //language=PostgreSQL
//    exec("INSERT INTO charm (id, \"name\", description, energy)\n" +
//      "SELECT tch.id, '', tch.description, tch.energy\n" +
//      "FROM tmp_charm tch LEFT JOIN tmp_client tcl ON tch.id = tcl.charm WHERE status = 0");

    //language=PostgreSQL
    exec("INSERT INTO client (id, surname, \"name\", patronymic, gender, birth_date, charm)\n" +
      "SELECT client_id, surname, \"name\", patronymic, gender, birth_date, charm\n" +
      "FROM TMP_CLIENT WHERE status = 0");

//    //language=PostgreSQL
//    exec("UPDATE charm ch SET \"name\" = tch.name\n" +
//      "FROM tmp_charm tch LEFT JOIN tmp_client tcl ON tch.id = tcl.charm WHERE status = 3");

    //language=PostgreSQL
    exec("UPDATE client c SET surname = s.surname\n" +
      "                 , \"name\" = s.\"name\"\n" +
      "                 , patronymic = s.patronymic\n" +
      "                 , birth_date = s.birth_date\n" +
      "FROM TMP_CLIENT s\n" +
      "WHERE c.id = s.client_id\n" +
      "AND s.status = 3");

    //language=PostgreSQL
    exec("UPDATE client SET actual = 1 WHERE id IN (\n" +
      "  SELECT client_id FROM TMP_CLIENT WHERE status = 0\n" +
      ")");

    //send report by ssh

    return 0;
  }

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

  public static void main(String[] args) throws Exception {
//    MigrationWorker migrationWorker = new MigrationWorker();
//    migrationWorker.download();
  }

  private int download() throws IOException, SAXException, SQLException {

    final AtomicBoolean working = new AtomicBoolean(true);
    final AtomicBoolean showStatus = new AtomicBoolean(false);

    final Thread see = new Thread(() -> {

      while (working.get()) {

        try {
          Thread.sleep(showStatusPingMillis);
        } catch (InterruptedException e) {
          break;
        }

        showStatus.set(true);

      }

    });
    see.start();


    // get file, read all files iteratively

    inputStream = new FileInputStream("build/out_files/from_cia_2018-02-27-154753-1-300.xml.tar.bz2");
    TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(inputStream));
    TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
    BufferedReader br;
    StringBuilder sb = new StringBuilder();
    while (currentEntry != null) {
      br = new BufferedReader(new InputStreamReader(tarInput));
      System.out.println("For File = " + currentEntry.getName());
      String line;
      ClientRecordParser clientRecordParser;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
      currentEntry = tarInput.getNextTarEntry();
    }
    String xmlContent = sb.toString();
//    System.out.println(xmlContent);

    // parse xml
    ClientRecordParser clientRecordParser = new ClientRecordParser();
    clientRecordParser.parseRecordData(xmlContent);

    List<ClientRecordsToSave> clientRecords = clientRecordParser.getClientRecords();
//    System.out.println(clientRecords.size());
//    for (ClientRecordsToSave clientRecord : clientRecords) {
//      System.out.println("surname: " + clientRecord.surname);
//      System.out.println("name: " + clientRecord.name);
//      System.out.println("dateOfBirth: " + clientRecord.dateOfBirth);
//      System.out.println();
//    }

    // write into tmp db
    connection.setAutoCommit(false);
    try (PreparedStatement ps = connection.prepareStatement("INSERT INTO tmp_client " +
        "(id, surname, name, patronymic, gender, birth_date, charm) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?) "
//      "ON CONFLICT (id) DO NOTHING"
//      "DO UPDATE SET surname = ?, name = ?, patronymic = ?, gender = ?, birth_date = ?, charm = ?"
    );

         PreparedStatement charmPS = connection.prepareStatement("INSERT INTO tmp_charm (id, name, description, energy) " +
           "VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, energy = ?");

         PreparedStatement phonePS = connection.prepareStatement("INSERT INTO tmp_phone (client, number, type) " +
           "VALUES (?, ?, ?) ON CONFLICT (client, number) DO UPDATE SET type = ?");

         PreparedStatement addrPS = connection.prepareStatement("INSERT INTO tmp_addr (client, type, street, house, flat) VALUES (?, ?, ?, ?, ?) " +
           "ON CONFLICT (client, type) DO UPDATE SET  street = ?, house = ?, flat = ?")
    ) {
      int batchSize = 0, recordsCount = 0;
      long startedAt = System.nanoTime();
      for (ClientRecordsToSave clientRecord : clientRecords) {
        clientRecord.charm.id = idGen.get().newId();

        ps.setString(1, clientRecord.id);
        ps.setString(2, clientRecord.surname);
        ps.setString(3, clientRecord.name);
        ps.setString(4, clientRecord.patronymic);
        ps.setString(5, clientRecord.gender.toString());
        ps.setDate(6, java.sql.Date.valueOf(clientRecord.dateOfBirth));
        ps.setString(7, clientRecord.charm.id);

//        ps.setString(8, clientRecord.surname);
//        ps.setString(9, clientRecord.name);
//        ps.setString(10, clientRecord.patronymic);
//        ps.setString(11, clientRecord.gender.toString());
//        ps.setDate(12, java.sql.Date.valueOf(clientRecord.dateOfBirth));
//        ps.setString(13, clientRecord.charm.id);

        charmPS.setString(1, clientRecord.charm.id);
        charmPS.setString(2, clientRecord.charm.name);
        charmPS.setString(3, "");
        charmPS.setDouble(4, 0);
        charmPS.setString(5, clientRecord.charm.name);
        charmPS.setString(6, "");
        charmPS.setDouble(7, 0);

        for (PhoneNumber phoneNumber : clientRecord.phoneNumbers) {
          phonePS.setString(1, clientRecord.id);
          phonePS.setString(2, phoneNumber.number);
          phonePS.setString(3, phoneNumber.phoneType.toString());
          phonePS.setString(4, phoneNumber.phoneType.toString());
          phonePS.executeUpdate();
          phonePS.addBatch();
        }

        addrPS.setString(1, clientRecord.id);
        addrPS.setString(2, clientRecord.addressF.type.toString());
        addrPS.setString(3, clientRecord.addressF.street);
        addrPS.setString(4, clientRecord.addressF.house);
        addrPS.setString(5, clientRecord.addressF.flat);
        addrPS.setString(6, clientRecord.addressF.street);
        addrPS.setString(7, clientRecord.addressF.house);
        addrPS.setString(8, clientRecord.addressF.flat);
        addrPS.executeUpdate();
        addrPS.addBatch();

        addrPS.setString(1, clientRecord.id);
        addrPS.setString(2, clientRecord.addressR.type.toString());
        addrPS.setString(3, clientRecord.addressR.street);
        addrPS.setString(4, clientRecord.addressR.house);
        addrPS.setString(5, clientRecord.addressR.flat);
        addrPS.setString(6, clientRecord.addressR.street);
        addrPS.setString(7, clientRecord.addressR.house);
        addrPS.setString(8, clientRecord.addressR.flat);
        addrPS.executeUpdate();
        addrPS.addBatch();

        charmPS.executeUpdate();
        charmPS.addBatch();

        ps.executeUpdate();
        ps.addBatch();
        batchSize++;
        recordsCount++;

        if (batchSize >= maxBatchSize) {
          charmPS.executeBatch();
          addrPS.executeBatch();
          phonePS.executeBatch();

          ps.executeBatch();
          connection.commit();
          batchSize = 0;
        }

        if (showStatus.get()) {
          showStatus.set(false);

          long now = System.nanoTime();
          info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
            + " : " + recordsPerSecond(recordsCount, now - startedAt));
        }
      }

      if (batchSize > 0) {
        charmPS.executeBatch();
        addrPS.executeBatch();
        phonePS.executeBatch();

        ps.executeBatch();
        connection.commit();
      }

      {
        long now = System.nanoTime();
        info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
          + " : " + recordsPerSecond(recordsCount, now - startedAt));
      }
    } finally {
      connection.setAutoCommit(true);
      working.set(false);
      see.interrupt();
    }


//    jdbcSandbox.get().execute(new InsertTmpClient(clientRecords));
//    jdbcSandbox.get().execute(new ConnectionCallback<Void>() {
//      @Override
//      public Void doInConnection(Connection connection) throws Exception {
//        connection.setAutoCommit(false);
//        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO tmp_client " +
//          "(id, surname, name, patronymic, gender, birth_date, charm) " +
//          "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) " +
//          "DO UPDATE SET surname = ?, name = ?, patronymic = ?, gender = ?, birth_date = ?, charm = ?");
//
//             PreparedStatement charmPS = connection.prepareStatement("INSERT INTO tmp_charm (id, name, description, energy) " +
//               "VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, energy = ?");
//
//             PreparedStatement phonePS = connection.prepareStatement("INSERT INTO tmp_phone (client, number, type) " +
//               "VALUES (?, ?, ?) ON CONFLICT (client, number) DO UPDATE SET type = ?");
//
//             PreparedStatement addrPS = connection.prepareStatement("INSERT INTO tmp_addr (client, type, street, house, flat) VALUES (?, ?, ?, ?, ?) " +
//               "ON CONFLICT (client, type) DO UPDATE SET  street = ?, house = ?, flat = ?")
//        ) {
//          int batchSize = 0, recordsCount = 0;
//          long startedAt = System.nanoTime();
//          int i = 0;
//          for (ClientRecordsToSave clientRecord : clientRecords) {
//            ps.setString(1, clientRecord.id);
//            ps.setString(2, clientRecord.surname);
//            ps.setString(3, clientRecord.name);
//            ps.setString(4, clientRecord.patronymic);
//            ps.setString(5, clientRecord.gender.toString());
//            ps.setDate(6, java.sql.Date.valueOf(clientRecord.dateOfBirth));
//            ps.setString(7, clientRecord.charm.id);
//
//            ps.setString(8, clientRecord.surname);
//            ps.setString(9, clientRecord.name);
//            ps.setString(10, clientRecord.patronymic);
//            ps.setString(11, clientRecord.gender.toString());
//            ps.setDate(12, java.sql.Date.valueOf(clientRecord.dateOfBirth));
//            ps.setString(13, clientRecord.charm.id);
//
//            charmPS.setString(1, "" + i++);
//            charmPS.setString(2, clientRecord.charm.name);
//            charmPS.setString(3, "");
//            charmPS.setDouble(4, 0);
//            charmPS.setString(5, clientRecord.charm.name);
//            charmPS.setString(6, "");
//            charmPS.setDouble(7, 0);
//
//            for (PhoneNumber phoneNumber : clientRecord.phoneNumbers) {
//              phonePS.setString(1, clientRecord.id);
//              phonePS.setString(2, phoneNumber.number);
//              phonePS.setString(3, phoneNumber.phoneType.toString());
//              phonePS.setString(4, phoneNumber.phoneType.toString());
//              phonePS.executeUpdate();
//              phonePS.addBatch();
//            }
//
//            addrPS.setString(1, clientRecord.id);
//            addrPS.setString(2, clientRecord.addressF.type.toString());
//            addrPS.setString(3, clientRecord.addressF.street);
//            addrPS.setString(4, clientRecord.addressF.house);
//            addrPS.setString(5, clientRecord.addressF.flat);
//            addrPS.setString(6, clientRecord.addressF.street);
//            addrPS.setString(7, clientRecord.addressF.house);
//            addrPS.setString(8, clientRecord.addressF.flat);
//            addrPS.executeUpdate();
//            addrPS.addBatch();
//
//            addrPS.setString(1, clientRecord.id);
//            addrPS.setString(2, clientRecord.addressR.type.toString());
//            addrPS.setString(3, clientRecord.addressR.street);
//            addrPS.setString(4, clientRecord.addressR.house);
//            addrPS.setString(5, clientRecord.addressR.flat);
//            addrPS.setString(6, clientRecord.addressR.street);
//            addrPS.setString(7, clientRecord.addressR.house);
//            addrPS.setString(8, clientRecord.addressR.flat);
//            addrPS.executeUpdate();
//            addrPS.addBatch();
//
//            charmPS.executeUpdate();
//            charmPS.addBatch();
//
//            ps.executeUpdate();
//            ps.addBatch();
//            batchSize++;
//            recordsCount++;
//
//            if (batchSize >= maxBatchSize) {
//              charmPS.executeBatch();
//              addrPS.executeBatch();
//              phonePS.executeBatch();
//
//              ps.executeBatch();
//              connection.commit();
//              batchSize = 0;
//            }
//
//            if (showStatus.get()) {
//              showStatus.set(false);
//
//              long now = System.nanoTime();
//              info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
//                + " : " + recordsPerSecond(recordsCount, now - startedAt));
//            }
//          }
//
//          if (batchSize > 0) {
//            charmPS.executeBatch();
//            addrPS.executeBatch();
//            phonePS.executeBatch();
//
//            ps.executeBatch();
//            connection.commit();
//          }
//
//          {
//            long now = System.nanoTime();
//            info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
//              + " : " + recordsPerSecond(recordsCount, now - startedAt));
//          }
//        } finally {
//          connection.setAutoCommit(true);
//          working.set(false);
//          see.interrupt();
//        }
//        return null;
//      }
//    });

    return 0;
  }

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
