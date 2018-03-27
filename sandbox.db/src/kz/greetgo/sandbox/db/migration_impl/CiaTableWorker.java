package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.util.TimeUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CiaTableWorker implements Closeable {

  public Connection connection;
  public int maxBatchSize;

  private PreparedStatement clientPS;
  private PreparedStatement phonePS;
  private PreparedStatement addrPS;
  private int clientBatchSize;

  public long startedAt;
  final AtomicBoolean working;
  final AtomicBoolean showStatus;

  private int addrBatchSize;
  private int phoneBatchSize;

  public final BlockingQueue<PhoneNumber> phonesQueue = new LinkedBlockingQueue<>();
  private final Thread phoneThread;

  private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

  public CiaTableWorker(Connection connection, int maxBatchSize, String clientTableName, String addrTableName, String phoneTableName)
    throws SQLException {

    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    clientPS = this.connection.prepareStatement("INSERT INTO " + clientTableName +
      " (number, cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
    );

    phonePS = this.connection.prepareStatement("INSERT INTO " + phoneTableName + " (client_num, phone_number, type) " +
      "VALUES (?, ?, ?)");

    addrPS = this.connection.prepareStatement("INSERT INTO " + addrTableName + " (cia_id, client_num, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?, ?)");

    working = new AtomicBoolean(true);
    showStatus = new AtomicBoolean(false);
    final Thread see = new Thread(() -> {

      while (working.get()) {

        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          break;
        }

        showStatus.set(true);

      }

    });
    see.start();


    phoneThread = new Thread(() -> {
      while (working.get()) {
        try {
          PhoneNumber phoneNumber = phonesQueue.take();
          if (phoneNumber.type == null) break;
          addToBatch(phoneNumber);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    phoneThread.start();
  }

  public void addToBatch(ClientTmp client) {

    try {
      int ind = 1;
      clientPS.setInt(ind++, client.id);
      clientPS.setString(ind++, client.cia_id);
      clientPS.setString(ind++, client.surname);
      clientPS.setString(ind++, client.name);

      clientPS.setString(ind++, client.patronymic);
      clientPS.setString(ind++, client.gender);
//      clientPS.setDate(ind++, client.birth_date != null ? java.sql.Date.valueOf(client.birth_date) : null);
      clientPS.setDate(ind++, client.birth_date != null ? new java.sql.Date(client.birth_date.getTime()) : null);
      clientPS.setString(ind, client.charm_name);

      clientPS.addBatch();
      clientBatchSize++;

      if (clientBatchSize >= maxBatchSize) {
        clientPS.executeBatch();

        connection.commit();
        clientBatchSize = 0;
      }

      if (showStatus.get()) {
        showStatus.set(false);

        long now = System.nanoTime();
        info(" -- downloaded records " + client.id + " for " + TimeUtils.showTime(now, startedAt)
          + " : " + TimeUtils.recordsPerSecond(client.id, now - startedAt));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  public void addToBatch(Address address) {

    try {
      int ind = 1;
      addrPS.setString(ind++, address.cia_id);
      addrPS.setInt(ind++, address.client_num);
      addrPS.setString(ind++, address.type);
      addrPS.setString(ind++, address.street);
      addrPS.setString(ind++, address.house);
      addrPS.setString(ind, address.flat);
      addrPS.addBatch();

      addrBatchSize++;

      if (addrBatchSize >= maxBatchSize) {
        addrPS.executeBatch();

        connection.commit();
        addrBatchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addToBatch(PhoneNumber phoneNumber) {

    try {

      int ind = 1;
      phonePS.setInt(ind++, phoneNumber.client_num);
      phonePS.setString(ind++, phoneNumber.phone_number);
      phonePS.setString(ind, phoneNumber.type.toString());
      phonePS.addBatch();

      phoneBatchSize++;

      if (phoneBatchSize >= maxBatchSize) {
        phonePS.executeBatch();

        connection.commit();
        phoneBatchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    try {

      if (clientBatchSize > 0) clientPS.executeBatch();
      if (addrBatchSize > 0) addrPS.executeBatch();

      // idle PhoneNumber object to indicate that no more numbers will be added
      phonesQueue.offer(new PhoneNumber());
      working.set(false);
      phoneThread.join();
      if (phoneBatchSize > 0) phonePS.executeBatch();

      if (clientBatchSize + addrBatchSize + phoneBatchSize > 0) this.connection.commit();

      clientPS.close();
      phonePS.close();
      addrPS.close();
    } catch (SQLException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
