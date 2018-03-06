package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.PhoneNumber;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CiaTableWorker implements Closeable {

  public Connection connection;
  public int maxBatchSize;

  private PreparedStatement clientPS;
  private PreparedStatement phonePS;
  private PreparedStatement addrPS;
  private int clientBatchSize;
  private int recordsCount;

  public Runnable execBatch;
  private int addrBatchSize;
  private int phoneBatchSize;

  public CiaTableWorker(Connection connection, int maxBatchSize) throws SQLException {
    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    clientPS = this.connection.prepareStatement("INSERT INTO tmp_client " +
      "(cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?) "
    );

    phonePS = this.connection.prepareStatement("INSERT INTO tmp_phone (cia_id, phone_number, type) " +
      "VALUES (?, ?, ?)");

    addrPS = this.connection.prepareStatement("INSERT INTO tmp_addr (cia_id, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?)");

    execBatch = () -> {
      try {
        if (clientBatchSize > 0) clientPS.executeBatch();

        if (addrBatchSize > 0) addrPS.executeBatch();

        if (phoneBatchSize > 0) phonePS.executeBatch();

        if (clientBatchSize + addrBatchSize + phoneBatchSize > 0) this.connection.commit();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    };
  }

  public void addToBatch(ClientRecordsToSave clientRecord) {

    try {
      int ind = 1;
      clientPS.setString(ind++, clientRecord.id);
      clientPS.setString(ind++, clientRecord.surname);
      clientPS.setString(ind++, clientRecord.name);

      clientPS.setString(ind++, clientRecord.patronymic);
      clientPS.setString(ind++, clientRecord.gender.toString());
      clientPS.setDate(ind++, clientRecord.dateOfBirth != null ? java.sql.Date.valueOf(clientRecord.dateOfBirth) : null);
      clientPS.setString(ind, clientRecord.charm.name);

      clientPS.addBatch();
      clientBatchSize++;
      recordsCount++;

      if (clientBatchSize >= maxBatchSize) {
        clientPS.executeBatch();

        connection.commit();
        clientBatchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addToBatch(Address address) {

    try {
      int ind = 1;
      addrPS.setString(ind++, address.id);
      addrPS.setString(ind++, address.type.toString());
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
      phonePS.setString(ind++, phoneNumber.id);
      phonePS.setString(ind++, phoneNumber.number);
      phonePS.setString(ind, phoneNumber.phoneType.toString());
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
      clientPS.close();
      phonePS.close();
      addrPS.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
