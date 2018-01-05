package kz.greetgo.sandbox.db.migration;


import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;

public class Migration implements ConnectionCallback<Void>{

  private ClientToSave client = new ClientToSave();

  public void parseRecordData() throws SAXException, IOException, ParserConfigurationException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();
    SAXPars saxp = new SAXPars();

    parser.parse(new File("/home/jgolibzhan/IdeaProjects/greetgo.sandbox/build/testxml.xml"), saxp);

    client = saxp.getClient();
  }

  public ClientToSave getClient(){
    return client;
  }



  @Override
  public Void doInConnection(Connection connection) throws Exception {
    return null;
  }
}
