package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientPhones;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SAXPars extends DefaultHandler {

  private final List<String> pathList = new ArrayList<>();

  private final ClientToSave client = new ClientToSave();
  ClientPhones ph = new ClientPhones();

  public ClientToSave getClient() {

    client.dateOfBirth = "1988-12-12";
    ph.home = "9898";
    ph.work = "1239898";
    ph.mobile.add("9432898");
    ClientAddress ad = new ClientAddress();
    client.phones = ph;
    client.regAddress = ad;
    client.factAddress = ad;

    return client;
  }

  protected String path() {
    StringBuilder sb = new StringBuilder();
    for (String pathElement : pathList) {
      sb.append('/').append(pathElement);
    }
    return sb.toString();
  }

  private StringBuilder text = null;

  protected String text() {
    if (text == null) return "";
    return text.toString();
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (text == null) text = new StringBuilder();
    text.append(ch, start, length);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    pathList.add(localName);

    try {
      startingTag(attributes);
    } catch (Exception e) {
      if (e instanceof SAXException) throw (SAXException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

    text = null;
  }


  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    pathList.remove(pathList.size() - 1);

    try {
      endedTag(localName);
    } catch (Exception e) {
      if (e instanceof SAXException) throw (SAXException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

    text = null;
  }

  protected void startingTag(Attributes attributes) {

    String path = path();

    if ("/client".equals(path)) {
      client.id = attributes.getValue("id");
      return;
    }
    if ("/client/name".equals(path)) {
      client.name = attributes.getValue("value");
      return;
    }
  }

  protected void endedTag(String tagName) throws Exception {

    String path = path() + "/" + tagName;

    if ("/client/homePhone".equals(path)) {

      ph.home = text();
      client.phones = ph;
      return;

    }
  }

}
