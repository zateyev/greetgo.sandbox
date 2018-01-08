package kz.greetgo.sandbox.db.register_impl.migration;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MigrationCia {
  public File inFile, errorsFile;
  public Connection connection;
  public int maxBatchSize = 5000;

  private final Logger logger = Logger.getLogger(getClass());

  private void exec(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      long startedAt = System.nanoTime();
      statement.execute(sql);
      logger.trace("SQL [" + (System.nanoTime() - startedAt) + "] " + sql);
    }
  }

  String clientTable;
  String addressTable;
  String phoneTable;

  public void migrate() throws Exception {
    createTempTables();
    uploadFileToTempTables();
    mainMigrationOperation();
    downloadErrors();
  }

  void createTempTables() throws Exception {
    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    clientTable = "tmp_client_" + date;
    addressTable = "tmp_client_address_" + date;
    phoneTable  = "tmp_client_phone_" + date;

    exec("create table " + clientTable + "(" +
      "  no bigint not null," +
      "  id varchar(50)," +
      "  surname varchar(300)," +
      "  name varchar(300)," +
      "  patronymic varchar(300)," +
      "  gender varchar(10)," +
      "  charm varchar(100)," +
      "  birth varchar(15)," +

      "  primary key(no)" +
      ")");

    exec(
      "create table " + addressTable + "(" +
        " client varchar(50)," +
        " type varchar(10)," +
        " street varchar(50), " +
        " house varchar(10)," +
        " flat varchar(10)," +

        " primary key(client, type)" +
        ")"
    );

  }


  void uploadFileToTempTables() throws Exception {

    try (CiaHandler ciaHandler = new CiaHandler(
      maxBatchSize,
      clientTable,
      addressTable,
      phoneTable,
      connection)) {

      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(ciaHandler);

      try (FileInputStream in = new FileInputStream(inFile)) {
        reader.parse(new InputSource(in));
      }
    }
  }

  void downloadErrors() {

  }

  void mainMigrationOperation() {

  }

}
