package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.util.SaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CiaDownloader extends SaxHandler implements AutoCloseable {

  public Connection connection;
  private ClientRecordsToSave clientRecord;
  public InputStream inputStream;
  public int maxBatchSize;

  private PreparedStatement clientPS;
  private PreparedStatement phonePS;
  private PreparedStatement addrPS;
  private int batchSize = 0;
  private int recordsCount;


  public CiaDownloader(InputStream inputStream, Connection connection) throws SQLException {
    this.inputStream = inputStream;
    this.connection = connection;

    clientPS = connection.prepareStatement("INSERT INTO tmp_client " +
      "(cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?) "
    );

    phonePS = connection.prepareStatement("INSERT INTO tmp_phone (cia_id, phone_number, type) " +
      "VALUES (?, ?, ?)");

    addrPS = connection.prepareStatement("INSERT INTO tmp_addr (cia_id, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?)");
  }

  public int downloadCia() throws SAXException, IOException, SQLException {
    if (inputStream == null) return 0;

    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(inputStream));
    return recordsCount;
  }

  @Override
  protected void startingTag(Attributes attributes) throws Exception {
    String path = path();

    switch (path) {
      case "/cia/client":
        clientRecord = new ClientRecordsToSave();
        clientRecord.phoneNumbers = new ArrayList<>();
        clientRecord.charm = new Charm();
        clientRecord.id = attributes.getValue("id");
        return;

      case "/cia/client/surname":
        clientRecord.surname = attributes.getValue("value");
        return;

      case "/cia/client/name":
        clientRecord.name = attributes.getValue("value");
        return;

      case "/cia/client/patronymic":
        clientRecord.patronymic = attributes.getValue("value");
        return;

      case "/cia/client/gender":
        clientRecord.gender = Gender.valueOf(attributes.getValue("value"));
        return;

      case "/cia/client/birth":
        clientRecord.dateOfBirth = attributes.getValue("value");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
          sdf.parse(clientRecord.dateOfBirth);
        } catch (ParseException e) {
          // Проглатывание ошибки
          clientRecord.dateOfBirth = null;
          return;
        }
        return;

      case "/cia/client/charm":
        clientRecord.charm.name = attributes.getValue("value");
        return;

      case "/cia/client/address/fact":
        clientRecord.addressF = new Address();
        clientRecord.addressF.type = AddressType.FACT;
        clientRecord.addressF.street = attributes.getValue("street");
        clientRecord.addressF.house = attributes.getValue("house");
        clientRecord.addressF.flat = attributes.getValue("flat");
        return;

      case "/cia/client/address/register":
        clientRecord.addressR = new Address();
        clientRecord.addressR.type = AddressType.REG;
        clientRecord.addressR.street = attributes.getValue("street");
        clientRecord.addressR.house = attributes.getValue("house");
        clientRecord.addressR.flat = attributes.getValue("flat");
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    switch (path) {
      case "/cia/client/workPhone": {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.phoneType = PhoneType.WORK;
        phoneNumber.number = text();
        clientRecord.phoneNumbers.add(phoneNumber);
        return;
      }

      case "/cia/client/mobilePhone": {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.phoneType = PhoneType.MOBILE;
        phoneNumber.number = text();
        clientRecord.phoneNumbers.add(phoneNumber);
        return;
      }

      case "/cia/client/homePhone": {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.phoneType = PhoneType.HOME;
        phoneNumber.number = text();
        clientRecord.phoneNumbers.add(phoneNumber);
        return;
      }

      case "/cia/client": {
        saveClient(clientRecord);
        return;
      }

      case "/cia": {
        if (batchSize > 0) {
          addrPS.executeBatch();
          phonePS.executeBatch();
          clientPS.executeBatch();

          connection.commit();
        }
      }
    }
  }

  private void saveClient(ClientRecordsToSave clientRecord) throws SQLException, IOException {

    clientPS.setString(1, clientRecord.id);
    clientPS.setString(2, clientRecord.surname);
    clientPS.setString(3, clientRecord.name);
    clientPS.setString(4, clientRecord.patronymic);
    clientPS.setString(5, clientRecord.gender.toString());
    clientPS.setDate(6, clientRecord.dateOfBirth != null ? java.sql.Date.valueOf(clientRecord.dateOfBirth) : null);
    clientPS.setString(7, clientRecord.charm.name);

    for (PhoneNumber phoneNumber : clientRecord.phoneNumbers) {
      phonePS.setString(1, clientRecord.id);
      phonePS.setString(2, phoneNumber.number);
      phonePS.setString(3, phoneNumber.phoneType.toString());
      phonePS.addBatch();
    }

    addrPS.setString(1, clientRecord.id);
    addrPS.setString(2, clientRecord.addressF.type.toString());
    addrPS.setString(3, clientRecord.addressF.street);
    addrPS.setString(4, clientRecord.addressF.house);
    addrPS.setString(5, clientRecord.addressF.flat);
    addrPS.addBatch();

    addrPS.setString(1, clientRecord.id);
    addrPS.setString(2, clientRecord.addressR.type.toString());
    addrPS.setString(3, clientRecord.addressR.street);
    addrPS.setString(4, clientRecord.addressR.house);
    addrPS.setString(5, clientRecord.addressR.flat);
    addrPS.addBatch();

    clientPS.addBatch();
    batchSize++;
    recordsCount++;

    if (batchSize >= maxBatchSize) {
      addrPS.executeBatch();
      phonePS.executeBatch();
      clientPS.executeBatch();

      connection.commit();
      batchSize = 0;
    }
  }

  @Override
  public void close() throws Exception {
    clientPS.close();
    phonePS.close();
    addrPS.close();
  }
}
