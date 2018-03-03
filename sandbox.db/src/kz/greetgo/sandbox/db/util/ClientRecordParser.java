package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.controller.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientRecordParser extends SaxHandler {

  private ClientRecordsToSave clientRecord;
  private List<ClientRecordsToSave> clientRecords;

  public List<ClientRecordsToSave> getClientRecords() {
    return clientRecords;
  }

  public ClientRecordParser() {
    clientRecords = new ArrayList<>();
  }

  public void parseRecordData(String recordData) throws SAXException, IOException {
    if (recordData == null) return;
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(new StringReader(recordData)));
  }

  public void parseRecordData2(InputStream inputStream) throws SAXException, IOException {
    if (inputStream == null) return;
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(inputStream));
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
        return;
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
        clientRecords.add(clientRecord);
        return;
      }
    }
  }
}
