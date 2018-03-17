package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneType;
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

public class CiaParser extends SaxHandler {

  private Client client;
  private InputStream inputStream;
  private CiaTableWorker ciaTableWorker;

  private int recordsNum;

  public CiaParser(InputStream inputStream, CiaTableWorker ciaTableWorker, int recordsNum) throws SQLException {
    this.inputStream = inputStream;
    this.ciaTableWorker = ciaTableWorker;
    this.recordsNum = recordsNum;
  }

  public int parseAndSave() throws SAXException, IOException, SQLException {
    if (inputStream == null) return 0;

    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(inputStream));
    return recordsNum;
  }

  @Override
  protected void startingTag(Attributes attributes) throws Exception {
    String path = path();

    switch (path) {
      case "/cia/client":
        client = new Client();
        client.cia_id = attributes.getValue("id");
        client.id = ++recordsNum;
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
//        client.dateOfBirth = attributes.getValue("value");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
          client.dateOfBirth = sdf.parse(attributes.getValue("value"));
        } catch (ParseException e) {
          // Проглатывание ошибки
//          client.dateOfBirth = null;
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
        addressFact.client_num = recordsNum;
//        ciaTableWorker.addToBatch(addressFact);
        ciaTableWorker.addressesQueue.offer(addressFact);
        return;

      case "/cia/client/address/register":
        Address addressReg = new Address("REG");
        addressReg.street = attributes.getValue("street");
        addressReg.house = attributes.getValue("house");
        addressReg.flat = attributes.getValue("flat");
        addressReg.cia_id = client.cia_id;
        addressReg.client_num = recordsNum;
//        ciaTableWorker.addToBatch(addressReg);
        ciaTableWorker.addressesQueue.offer(addressReg);
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    if (path.endsWith("Phone")) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.client_num = recordsNum;
      phoneNumber.number = text();
      switch (path) {
        case "/cia/client/workPhone": {
          phoneNumber.type = PhoneType.WORK;
          break;
        }

        case "/cia/client/mobilePhone": {
          phoneNumber.type = PhoneType.MOBILE;
          break;
        }

        case "/cia/client/homePhone": {
          phoneNumber.type = PhoneType.HOME;
          break;
        }
      }
//      ciaTableWorker.addToBatch(phoneNumber);
      ciaTableWorker.phonesQueue.offer(phoneNumber);
    } else if ("/cia/client".equals(path)) {
      ciaTableWorker.addToBatch(client);
    }

//    switch (path) {
//      case "/cia/client/workPhone": {
//        PhoneNumber phoneNumber = new PhoneNumber();
//        phoneNumber.type = PhoneType.WORK;
//        phoneNumber.client_num = recordsNum;
//        phoneNumber.number = text();
//        ciaTableWorker.addToBatch(phoneNumber);
//        return;
//      }
//
//      case "/cia/client/mobilePhone": {
//        PhoneNumber phoneNumber = new PhoneNumber();
//        phoneNumber.type = PhoneType.MOBILE;
//        phoneNumber.client_num = recordsNum;
//        phoneNumber.number = text();
//        ciaTableWorker.addToBatch(phoneNumber);
//        return;
//      }
//
//      case "/cia/client/homePhone": {
//        PhoneNumber phoneNumber = new PhoneNumber();
//        phoneNumber.type = PhoneType.HOME;
//        phoneNumber.client_num = recordsNum;
//        phoneNumber.number = text();
//        ciaTableWorker.addToBatch(phoneNumber);
//        return;
//      }
//
//      case "/cia/client": {
//        ciaTableWorker.addToBatch(client);
//      }
//    }
  }
}
