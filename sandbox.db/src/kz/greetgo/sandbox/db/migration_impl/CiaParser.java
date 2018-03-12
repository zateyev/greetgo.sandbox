package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
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

  private Client client;
  public InputStream inputStream;
  private CiaTableWorker ciaTableWorker;

  private int recordsCount;

  public CiaParser(InputStream inputStream, CiaTableWorker ciaTableWorker) throws SQLException {
    this.inputStream = inputStream;
    this.ciaTableWorker = ciaTableWorker;
  }

  public int parseAndSave() throws SAXException, IOException, SQLException {
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
        client = new Client();
        client.cia_id = attributes.getValue("id");
        recordsCount++;
        return;

      case "/cia/client/surname":
        client.surname = attributes.getValue("value");
        return;

      case "/cia/client/name":
        client.name = attributes.getValue("value");
        return;

      case "/cia/client/patronymic":
        client.patronymic = attributes.getValue("value");
        return;

      case "/cia/client/gender":
        client.gender = attributes.getValue("value");
        return;

      case "/cia/client/birth":
        client.dateOfBirth = attributes.getValue("value");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
          sdf.parse(client.dateOfBirth);
        } catch (ParseException e) {
          // Проглатывание ошибки
          client.dateOfBirth = null;
          return;
        }
        return;

      case "/cia/client/charm":
        client.charmName = attributes.getValue("value");
        return;

      case "/cia/client/address/fact":
        Address addressFact = new Address("FACT");
        addressFact.street = attributes.getValue("street");
        addressFact.house = attributes.getValue("house");
        addressFact.flat = attributes.getValue("flat");
        addressFact.cia_id = client.cia_id;
        sendTo(ciaTableWorker::addToBatch, addressFact);
        return;

      case "/cia/client/address/register":
        Address addressReg = new Address("REG");
        addressReg.street = attributes.getValue("street");
        addressReg.house = attributes.getValue("house");
        addressReg.flat = attributes.getValue("flat");
        addressReg.cia_id = client.cia_id;
        sendTo(ciaTableWorker::addToBatch, addressReg);
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    switch (path) {
      case "/cia/client/workPhone": {
        PhoneNumber phoneNumber = new PhoneNumber("WORK");
        phoneNumber.cia_id = client.cia_id;
        phoneNumber.number = text();
        sendTo(ciaTableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client/mobilePhone": {
        PhoneNumber phoneNumber = new PhoneNumber("MOBILE");
        phoneNumber.cia_id = client.cia_id;
        phoneNumber.number = text();
        sendTo(ciaTableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client/homePhone": {
        PhoneNumber phoneNumber = new PhoneNumber("HOME");
        phoneNumber.cia_id = client.cia_id;
        phoneNumber.number = text();
        sendTo(ciaTableWorker::addToBatch, phoneNumber);
        return;
      }

      case "/cia/client": {
        sendTo(ciaTableWorker::addToBatch, client);
      }
    }
  }

  private void sendTo(final Consumer<Client> func, Client client) {
    func.accept(client);
  }

  private void sendTo(final Consumer<Address> func, Address address) {
    func.accept(address);
  }

  private void sendTo(final Consumer<PhoneNumber> func, PhoneNumber phoneNumber) {
    func.accept(phoneNumber);
  }
}
