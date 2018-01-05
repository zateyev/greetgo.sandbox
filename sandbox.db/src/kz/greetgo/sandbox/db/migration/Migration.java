package kz.greetgo.sandbox.db.migration;


import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Migration {
  public BeanGetter<JdbcSandbox> jdbc;

  SAXParsClient saxp = new SAXParsClient();

  public void migrate() throws ParserConfigurationException, SAXException, IOException {
    parseRecordData();
    migrateToDB(saxp.getClientList());
  }

  private void migrateToDB(List<ClientToSave> clientList) {
    jdbc.get().execute(new InsertClients(clientList));
  }

  public void parseRecordData() throws SAXException, IOException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();

    parser.parse(new File("/home/jgolibzhan/IdeaProjects/greetgo.sandbox/build/testxml.xml"), saxp);
  }

}
