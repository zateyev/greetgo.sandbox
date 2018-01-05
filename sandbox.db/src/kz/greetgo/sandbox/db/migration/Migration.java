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
import java.util.ArrayList;
import java.util.List;

public class Migration {
  public BeanGetter<JdbcSandbox> jdbc;

  List<ClientToSave> list = new ArrayList<>();

  public void migrate() throws ParserConfigurationException, SAXException, IOException {
    parseRecordData();
  }

  private void migrateToDB(SAXParsClient saxParsClient) {
    jdbc.get().execute(new InsertClients(saxParsClient));
  }

  public void parseRecordData() throws SAXException, IOException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();
    SAXParsClient saxp = new SAXParsClient();
    parser.parse(new File("/home/jgolibzhan/IdeaProjects/greetgo.sandbox/build/testxml.xml"), saxp);

    migrateToDB(saxp);
  }

}
