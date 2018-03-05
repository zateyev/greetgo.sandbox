package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.PhoneNumber;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableWorker implements Closeable {

  public Connection connection;
  public int maxBatchSize = 50_000;

  private PreparedStatement clientPS;
  private PreparedStatement phonePS;
  private PreparedStatement addrPS;
  private int batchSize = 0;
  private int recordsCount;

  public Runnable execBatch;
  private int addrBatchSize;
  private int phoneBatchSize;

  public TableWorker(Connection connection) throws SQLException {
    this.connection = connection;
    clientPS = this.connection.prepareStatement("INSERT INTO tmp_client " +
      "(cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?) "
    );

    phonePS = this.connection.prepareStatement("INSERT INTO tmp_phone (cia_id, phone_number, type) " +
      "VALUES (?, ?, ?)");

    addrPS = this.connection.prepareStatement("INSERT INTO tmp_addr (cia_id, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?)");

    execBatch = () -> {
      if (batchSize > 0) {
        try {
//          phonePS.executeBatch();
          clientPS.executeBatch();

          this.connection.commit();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

      if (addrBatchSize > 0) {
        try {
          addrPS.executeBatch();
          this.connection.commit();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

      if (phoneBatchSize > 0) {
        try {
          phonePS.executeBatch();
          this.connection.commit();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
  }

  public void addToBatchClient(ClientRecordsToSave clientRecord) {

    try {
      clientPS.setString(1, clientRecord.id);
      clientPS.setString(2, clientRecord.surname);
      clientPS.setString(3, clientRecord.name);

      clientPS.setString(4, clientRecord.patronymic);
      clientPS.setString(5, clientRecord.gender.toString());
      clientPS.setDate(6, clientRecord.dateOfBirth != null ? java.sql.Date.valueOf(clientRecord.dateOfBirth) : null);
      clientPS.setString(7, clientRecord.charm.name);

      clientPS.addBatch();
      batchSize++;
      recordsCount++;

      if (batchSize >= maxBatchSize) {
//        phonePS.executeBatch();
        clientPS.executeBatch();

        connection.commit();
        batchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addToBatchAddr(Address address) {

    try {
      addrPS.setString(1, address.id);
      addrPS.setString(2, address.type.toString());
      addrPS.setString(3, address.street);
      addrPS.setString(4, address.house);
      addrPS.setString(5, address.flat);
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

  public void addToBatchPhone(PhoneNumber phoneNumber) {

    try {

      phonePS.setString(1, phoneNumber.id);
      phonePS.setString(2, phoneNumber.number);
      phonePS.setString(3, phoneNumber.phoneType.toString());
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
