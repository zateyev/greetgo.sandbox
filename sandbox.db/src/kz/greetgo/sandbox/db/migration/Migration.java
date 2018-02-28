package kz.greetgo.sandbox.db.migration;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.ClientRecordParser;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

@Bean
public class Migration {

  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<ClientDao> tmpClientDao;

  private String tmpClientTable;

  public int portionSize = 1_000_000;
  public int downloadMaxBatchSize = 50_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    // create tmp tables

    int recordsSize = download();

    {
      long now = System.nanoTime();
      info("Downloading of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

    if (recordsSize == 0) return 0;

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }

    return recordsSize;
  }

  private long migrateFromTmp() {
    return 0;
  }

  public static void main(String[] args) throws Exception {
//    Migration migration = new Migration();
//    System.out.println(migration.jdbcSandbox);
    Migration migration = new Migration();
    migration.download();
  }

  private int download() throws IOException, SAXException {

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
    InputStream fis = new FileInputStream("build/out_files/from_cia_2018-02-27-154753-1-300.xml.tar.bz2");
    TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(fis));
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
    System.out.println(xmlContent);

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
//    jdbcSandbox.get().execute(new InsertTmpClient(clientRecords));
    jdbcSandbox.get().execute(new ConnectionCallback<Void>() {
      @Override
      public Void doInConnection(Connection connection) throws Exception {
        connection.setAutoCommit(false);
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO tmp_client " +
          "(id, surname, name, patronymic, gender, birth_date, charm) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) " +
          "DO UPDATE SET surname = ?, name = ?, patronymic = ?, gender = ?, birth_date = ?, charm = ?");

             PreparedStatement charmPS = connection.prepareStatement("INSERT INTO tmp_charm (id, name, description, energy) " +
               "VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, energy = ?")) {
          int batchSize = 0, recordsCount = 0;
          long startedAt = System.nanoTime();
          int i = 0;
          for (ClientRecordsToSave clientRecord : clientRecords) {
            if (clientRecord.id == null) i++;
            ps.setString(1, clientRecord.id);
            ps.setString(2, clientRecord.surname);
            ps.setString(3, clientRecord.name);
            ps.setString(4, clientRecord.patronymic);
            ps.setString(5, clientRecord.gender.toString());
            ps.setDate(6, java.sql.Date.valueOf(clientRecord.dateOfBirth));
            ps.setString(7, clientRecord.charm.id);

            ps.setString(8, clientRecord.surname);
            ps.setString(9, clientRecord.name);
            ps.setString(10, clientRecord.patronymic);
            ps.setString(11, clientRecord.gender.toString());
            ps.setDate(12, java.sql.Date.valueOf(clientRecord.dateOfBirth));
            ps.setString(13, clientRecord.charm.id);

            charmPS.setString(1, "" + i);
            charmPS.setString(2, clientRecord.charm.name);
            charmPS.setString(3, "");
            charmPS.setDouble(4, i * i);
            charmPS.setString(5, clientRecord.charm.name);
            charmPS.setString(6, "");
            charmPS.setDouble(7, i * i);

            charmPS.executeUpdate();
            charmPS.addBatch();

            ps.executeUpdate();
            ps.addBatch();
            batchSize++;
            recordsCount++;

            if (batchSize >= downloadMaxBatchSize) {
              charmPS.executeBatch();

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
          System.out.println("Count of null ids: " + i);

          if (batchSize > 0) {
            charmPS.executeBatch();

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
        return null;
      }
    });

    return 0;
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
