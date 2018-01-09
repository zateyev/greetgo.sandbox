package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class MigrationCia {
  public File inFile, errorsFile;
  public Connection connection;
  public int maxBatchSize = 5000;

  private final Logger logger = Logger.getLogger(getClass());
  private IdGenerator id = new IdGenerator();

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
    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

    clientTable = "tmp_client_" + date;
    addressTable = "tmp_client_address_" + date;
    phoneTable = "tmp_client_phone_" + date;

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

    exec(
      "create table " + phoneTable + "(" +
        " client varchar(50)," +
        " type varchar(10)," +
        " number varchar(50), " +

        " primary key(client, number)" +
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

  void mainMigrationOperation() throws SQLException {

    String charmId;

    String insertClient = "" +
      " insert into client (id, name, surname, patronymic, current_gender, charm_id, birth_date, cia_id, actual)" +
      " values(?, ?, ?, ?, ?, ?, ?, ?, 1)" +
      " on conflict(cia_id) do " +
      " update set name = EXCLUDED.name," +
      " surname = excluded.surname," +
      " patronymic = excluded.patronymic," +
      " current_gender = excluded.current_gender," +
      " charm_id = excluded.charm_id," +
      " birth_date = excluded.birth_date," +
      " actual = 1";

    String insertCharm = "insert into charm(id, name, actual) VALUES (?, ?, 1)";
    String getCharms = "select id, name from charm where actual = 1";

    Map<String, String> charms = new HashMap<>();

    try (PreparedStatement charmPS = connection.prepareStatement(getCharms)) {
      try (ResultSet rs = charmPS.executeQuery()) {
        while (rs.next()) {

          charms.put(rs.getString(2), rs.getString(1));

        }
      }
    }

    try (PreparedStatement tmpPS = connection.prepareStatement(
      "select * from " + clientTable)
    ) {

      try (ResultSet tmpRS = tmpPS.executeQuery()) {

        while (tmpRS.next()) {
          if(charms.containsKey(tmpRS.getString("charm"))){
            charmId = charms.get(tmpRS.getString("charm"));
          }
          else{
            charmId = id.newId();

            try(PreparedStatement charmPS = connection.prepareStatement(insertCharm)){
              charmPS.setString(1, charmId);
              charmPS.setString(2, tmpRS.getString("charm"));
              charmPS.execute();
            }
          }

          try (PreparedStatement mainPS = connection.prepareStatement(insertClient)) {

            {
              int index = 1;
              mainPS.setString(index++, id.newId());
              mainPS.setString(index++, tmpRS.getString("name"));
              mainPS.setString(index++, tmpRS.getString("surname"));
              mainPS.setString(index++, tmpRS.getString("patronymic"));
              mainPS.setString(index++, tmpRS.getString("gender").toLowerCase());
              mainPS.setString(index++, charmId);
              mainPS.setDate(index++, getDate(tmpRS.getString("birth")));
              mainPS.setString(index++, tmpRS.getString("id"));
              mainPS.execute();
            }

          }

        }

      }

    }

  }


  private java.sql.Date getDate(String date) {
    return java.sql.Date.valueOf(date);
  }

}
