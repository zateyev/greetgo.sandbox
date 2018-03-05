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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class CiaParser extends SaxHandler {

  private ClientRecordsToSave clientRecord;
  private Address addressFact;
  private Address addressReg;
  private PhoneNumber phoneNumber;

  public InputStream inputStream;

  private TableWorker tableWorker;

  public CiaParser(InputStream inputStream, TableWorker tableWorker) throws SQLException {
    this.inputStream = inputStream;
    this.tableWorker = tableWorker;

    clientRecord = new ClientRecordsToSave();
    clientRecord.charm = new Charm();
    addressFact = new Address();
    addressFact.type = AddressType.FACT;
    addressReg = new Address();
    addressReg.type = AddressType.REG;
    phoneNumber = new PhoneNumber();
  }

  public int parseAndSave() throws SAXException, IOException, SQLException {
    if (inputStream == null) return 0;

    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(inputStream));
    return 0;
  }

  @Override
  protected void startingTag(Attributes attributes) throws Exception {
    String path = path();

    switch (path) {
      case "/cia/client":
        clientRecord.id = attributes.getValue("id");
        addressFact.id = clientRecord.id;
        addressReg.id = clientRecord.id;
        phoneNumber.id = clientRecord.id;
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
        addressFact.street = attributes.getValue("street");
        addressFact.house = attributes.getValue("house");
        addressFact.flat = attributes.getValue("flat");
        sendTo(tableWorker::addToBatch, addressFact);
        return;

      case "/cia/client/address/register":
        addressReg.street = attributes.getValue("street");
        addressReg.house = attributes.getValue("house");
        addressReg.flat = attributes.getValue("flat");
        sendTo(tableWorker::addToBatch, addressReg);
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    switch (path) {
      case "/cia/client/workPhone": {
        phoneNumber.phoneType = PhoneType.WORK;
        phoneNumber.number = text();
        sendTo(tableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client/mobilePhone": {
        phoneNumber.phoneType = PhoneType.MOBILE;
        phoneNumber.number = text();
        sendTo(tableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client/homePhone": {
        phoneNumber.phoneType = PhoneType.HOME;
        phoneNumber.number = text();
        sendTo(tableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client": {
        sendTo(tableWorker::addToBatch, clientRecord);
        return;
      }

      case "/cia": {
        tableWorker.execBatch.run();
      }
    }
  }

  private void sendTo(final Consumer<ClientRecordsToSave> func, ClientRecordsToSave clientRecord) {
    func.accept(clientRecord);
  }

  private void sendTo(final Consumer<Address> func, Address address) {
    func.accept(address);
  }

  private void sendTo(final Consumer<PhoneNumber> func, PhoneNumber phoneNumber) {
    func.accept(phoneNumber);
  }
}
