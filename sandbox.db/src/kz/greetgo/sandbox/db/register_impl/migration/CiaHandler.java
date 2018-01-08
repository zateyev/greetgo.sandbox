package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.register_impl.migration.models.Address;
import kz.greetgo.sandbox.db.register_impl.migration.models.Client;
import kz.greetgo.sandbox.db.register_impl.migration.models.Phone;
import org.xml.sax.Attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CiaHandler extends TagHandler implements AutoCloseable {

  private final int maxBatchSize;

  private PreparedStatement clientPS, addressPS;
  private final Connection connection;

  public CiaHandler(int maxBatchSize,
                    String clientTable,
                    String addressTable,
                    String phoneTable,
                    Connection connection) throws SQLException {

    this.maxBatchSize = maxBatchSize;
    this.connection = connection;
    connection.setAutoCommit(false);
    clientPS = connection.prepareStatement(
      "insert into " + clientTable + " (no, id, surname, name, patronymic, gender, charm, birth) " +
        "values (?, ?, ?, ?, ?, ?, ?, ?)"
    );

    addressPS = connection.prepareStatement(
      "insert into " + addressTable + " (client, type, street, house, flat) " +
        "VALUES (?, ?, ?, ?, ?)"
    );
  }

  int batchSize = 0;
  int recordsCount = 0;

  private void addBatch() throws SQLException {

    clientPS.setLong(1, no);
    clientPS.setString(2, client.id);
    clientPS.setString(3, client.surname);
    clientPS.setString(4, client.name);
    clientPS.setString(5, client.patronymic);
    clientPS.setString(6, client.gender);
    clientPS.setString(7, client.charm);
    clientPS.setString(8, client.birth);
    clientPS.addBatch();

    addressPS.setString(1, factAddress.client);
    addressPS.setString(2, "fact");
    addressPS.setString(3, factAddress.street);
    addressPS.setString(4, factAddress.house);
    addressPS.setString(5, factAddress.flat);
    addressPS.addBatch();

    addressPS.setString(1, regAddress.client);
    addressPS.setString(2, "reg");
    addressPS.setString(3, regAddress.street);
    addressPS.setString(4, regAddress.house);
    addressPS.setString(5, regAddress.flat);

    addressPS.addBatch();

    recordsCount++;
    batchSize++;

    if (batchSize >= maxBatchSize) {
      clientPS.executeBatch();
      addressPS.executeBatch();
      connection.commit();
      batchSize = 0;
    }
  }

  @Override
  public void close() throws Exception {

    if (batchSize > 0) {

      clientPS.executeBatch();
      addressPS.executeBatch();

      connection.commit();
      batchSize = 0;
    }

    clientPS.close();
    connection.setAutoCommit(true);
  }

  Client client = new Client();
  Address factAddress = new Address();
  Address regAddress = new Address();

  long no = 0;

  @Override
  protected void startTag(Attributes attributes) throws Exception {
    String path = path();

    if ("/cia/client".equals(path)) {
      client.id = attributes.getValue("id");
      factAddress.client = regAddress.client = attributes.getValue("id");
      no++;
      return;
    }

    if ("/cia/client/surname".equals(path)) {
      client.surname = attributes.getValue("value");
      return;
    }

    if ("/cia/client/name".equals(path)) {
      client.name = attributes.getValue("value");
      return;
    }

    if ("/cia/client/patronymic".equals(path)) {
      client.patronymic = attributes.getValue("value");
      return;
    }

    if ("/cia/client/gender".equals(path)) {
      client.gender = attributes.getValue("value");
      return;
    }

    if ("/cia/client/charm".equals(path)) {
      client.charm = attributes.getValue("value");
      return;
    }

    if ("/cia/client/birth".equals(path)) {
      client.birth = attributes.getValue("value");
      return;
    }

    if ("/cia/client/address/fact".equals(path)) {
      factAddress.street = attributes.getValue("street");
      factAddress.house = attributes.getValue("house");
      factAddress.flat = attributes.getValue("flat");
      return;
    }

    if ("/cia/client/address/register".equals(path)) {
      regAddress.street = attributes.getValue("street");
      regAddress.house = attributes.getValue("house");
      regAddress.flat = attributes.getValue("flat");
      return;
    }

  }

  @Override
  protected void endTag() throws Exception {
    String path = path();

    if ("/cia/client".equals(path)) {

      addBatch();

      client = new Client();

      return;
    }


  }


}
