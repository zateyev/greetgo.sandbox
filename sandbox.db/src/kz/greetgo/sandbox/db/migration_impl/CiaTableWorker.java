package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;

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

  private int addrBatchSize;
  private int phoneBatchSize;

  public CiaTableWorker(Connection connection, int maxBatchSize, String clientTableName, String addrTableName, String phoneTableName) throws SQLException {
    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    clientPS = this.connection.prepareStatement("INSERT INTO " + clientTableName +
      " (number, cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
    );

    phonePS = this.connection.prepareStatement("INSERT INTO " + phoneTableName + " (cia_id, phone_number, type) " +
      "VALUES (?, ?, ?)");

    addrPS = this.connection.prepareStatement("INSERT INTO " + addrTableName + " (cia_id, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?)");
  }

  public void addToBatch(Client client) {

    try {
      int ind = 1;
      clientPS.setInt(ind++, client.id);
      clientPS.setString(ind++, client.cia_id);
      clientPS.setString(ind++, client.surname);
      clientPS.setString(ind++, client.name);

      clientPS.setString(ind++, client.patronymic);
      clientPS.setString(ind++, client.gender);
      clientPS.setDate(ind++, client.dateOfBirth != null ? java.sql.Date.valueOf(client.dateOfBirth) : null);
      clientPS.setString(ind, client.charmName);

      clientPS.addBatch();
      clientBatchSize++;

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
      addrPS.setString(ind++, address.cia_id);
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
      phonePS.setString(ind++, phoneNumber.cia_id);
      phonePS.setString(ind++, phoneNumber.number);
      phonePS.setString(ind, phoneNumber.type);
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
      if (phoneBatchSize > 0) phonePS.executeBatch();
      if (clientBatchSize + addrBatchSize + phoneBatchSize > 0) this.connection.commit();

      clientPS.close();
      phonePS.close();
      addrPS.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
