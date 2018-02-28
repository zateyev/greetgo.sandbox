package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.PhoneNumber;
import kz.greetgo.sandbox.controller.model.PhoneType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

  @Override
  protected void startingTag(Attributes attributes) throws Exception {
    String path = path();

    // TODO: переписать в switch
    switch (path){
      case "":
        break;

    }

    if ("/cia/client".equals(path)) {
      clientRecord = new ClientRecordsToSave();
      clientRecord.phoneNumbers = new ArrayList<>();
      clientRecord.charm = new Charm();
      clientRecord.id = attributes.getValue("id");
      return;
    }

    if ("/cia/client/surname".equals(path)) {
      clientRecord.surname = attributes.getValue("value");
      return;
    }

    if ("/cia/client/name".equals(path)) {
      clientRecord.name = attributes.getValue("value");
      return;
    }

    if ("/cia/client/patronymic".equals(path)) {
      clientRecord.patronymic = attributes.getValue("value");
      return;
    }

    if ("/cia/client/charm".equals(path)) {
      clientRecord.charm.name = attributes.getValue("value");
      return;
    }

    if ("/cia/client/birth".equals(path)) {
      clientRecord.dateOfBirth = attributes.getValue("value");
      return;
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    if ("/cia/client/workPhone".equals(path)) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.phoneType = PhoneType.WORK;
      phoneNumber.number = text();
      clientRecord.phoneNumbers.add(phoneNumber);
      return;
    }

    if ("/cia/client/mobilePhone".equals(path)) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.phoneType = PhoneType.MOBILE;
      phoneNumber.number = text();
      clientRecord.phoneNumbers.add(phoneNumber);
      return;
    }

    if ("/cia/client/homePhone".equals(path)) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.phoneType = PhoneType.HOME;
      phoneNumber.number = text();
      clientRecord.phoneNumbers.add(phoneNumber);
      return;
    }

    if ("/cia/client".equals(path)) {
      clientRecords.add(clientRecord);
      return;
    }
  }
}
