package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.report.SqlExecutionTime.SqlExecutionTimeView;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class MigrationCia {
  public File inFile, errorsFile;
  public Connection connection;
  public int maxBatchSize = 5000;
  public SqlExecutionTimeView view;

  private final Logger logger = Logger.getLogger(getClass());

  public MigrationCia() throws FileNotFoundException {}

  private void exec(String sql) throws SQLException {

    sql = sql.replaceAll("TMP_CLIENT", clientTable);
    sql = sql.replaceAll("TMP_ADDRESS", addressTable);
    sql = sql.replaceAll("TMP_PHONE", phoneTable);

    try (Statement statement = connection.createStatement()) {
      long startedAt = System.nanoTime();

      statement.execute(sql);

      long elapsed = System.nanoTime() - startedAt;
      double seconds = (double) elapsed / 1000000000.0;

      view.append(seconds, sql);
      logger.trace("SQL [" + seconds + "] " + sql);
    }
  }

  String clientTable, addressTable, phoneTable;
  StringBuilder errorLog = new StringBuilder();


  public void migrate() throws Exception {
    createTempTables();
    uploadFileToTempTables();
    mainMigrationOperation();
    downloadErrors();
  }

  public void getExecutedTime() throws SQLException {
    view.finish();
  }

  void createTempTables() throws Exception {

    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

    clientTable = "tmp_client_" + date + "_" + RND.intStr(3);
    addressTable = "tmp_client_address_" + date + "_" + RND.intStr(3);
    phoneTable = "tmp_client_phone_" + date + "_" + RND.intStr(3);

    exec("create table " + clientTable + "(" +
      "  no bigint not null," +
      "  cia_id varchar(50)," +
      "  surname varchar(300)," +
      "  name varchar(300)," +
      "  patronymic varchar(300)," +
      "  gender varchar(10)," +
      "  charm varchar(100)," +
      "  birth varchar(15)," +
      "  generatedId varchar(50)," +
      "  status varchar(100) default 'JUST_INSERTED'," +
      "  error varchar(100) default null," +

      "  primary key(no)" +
      ")");

    exec(
      "create table " + addressTable + "(" +
        " no bigint not null, " +
        " client varchar(50)," +
        " type varchar(10)," +
        " street varchar(50), " +
        " house varchar(10)," +
        " flat varchar(10)," +
        " error varchar(300) default null," +

        " primary key(no, type)" +
        ")"
    );

    exec(
      "create table " + phoneTable + "(" +
        " no bigint not null," +
        " client varchar(50)," +
        " type varchar(10)," +
        " number varchar(50) " +

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
      } catch (SAXParseException e) {
        errorLog.append("SAX Parse Error: ").append(e.getMessage()).append("\n");


        errorLog.append("At line [").append(e.getLineNumber()).append("] ")
          .append("Column number: [").append(e.getColumnNumber()).append("]\n");
      }
    }
  }

  void downloadErrors() throws Exception {

    try (FileWriter out = new FileWriter(errorsFile, true)) {

      out.write(errorLog.toString());
      ClientErrorWriter ew = new ClientErrorWriter(
        out, connection, clientTable
      );

    }
  }

  void mainMigrationOperation() throws SQLException {

    exec(
      " update TMP_CLIENT set\n" +
        " error = '[Name is null] Client fields cannot be null'\n" +
        " where \n" +
        " name is null and error is null"
    );

    exec(
      "  update TMP_CLIENT set\n" +
        " error = '[Surname is null] Client fields cannot be null'\n" +
        " where \n" +
        " surname is null and error is null"
    );

    exec(
      "   update TMP_CLIENT set\n" +
        " error = '[Gender is null] Client fields cannot be null'\n" +
        " where \n" +
        " gender is null and error is null"
    );

    exec(
      " update TMP_CLIENT set\n" +
        " error = '[Birth date is null] Client fields cannot be null'\n" +
        " where \n" +
        " birth is null and error is null"
    );

    exec(
      " update TMP_CLIENT set\n" +
        " error = '[Charm is null] Client fields cannot be null'\n" +
        " where \n" +
        " charm is null and error is null"
    );

    exec(
      " update TMP_ADDRESS set\n" +
        " error = '[Street is null] Address fields cannot be null'\n" +
        " where street is null and error is null"
    );

    exec(
      " update TMP_ADDRESS set\n" +
        " error = '[House is null] Address fields cannot be null'\n" +
        " where house is null and error is null"
    );

    exec(
      "  update TMP_ADDRESS set\n" +
        " error = '[Flat is null] Address fields cannot be null'\n" +
        " where flat is null and error is null"
    );

    exec(
      "update TMP_CLIENT as tmp\n" +
        "set error = 'Phones not found for this client'\n" +
        "from (select no from TMP_CLIENT as tm\n" +
        "except select no from TMP_PHONE as ph) as a\n" +
        "where tmp.no = a.no"
    );

    exec(
      "update TMP_CLIENT as tmp \n" +
        "set error = 'Fact or registered address have null field for this client'\n" +
        "from TMP_ADDRESS as ad\n" +
        "where tmp.\"no\" = ad.\"no\" \n" +
        "and ad.error is not null"
    );

    exec(
      "insert into charm (name, id, actual) \n" +
        " select  distinct on(charm) charm, tmp.generatedId as id, 1 as actual \n" +
        " from TMP_CLIENT as tmp where tmp.charm not in(select name from charm where actual = 1) " +
        "and error is null group by charm, generatedId"
    );

    exec(
      "with a as(\n" +
        "SELECT\n" +
        " row_number() over(partition by cia_id order by no desc) as num,\n" +
        " * FROM TMP_CLIENT\n" +
        " )\n" +
        " update TMP_CLIENT as tmp\n" +
        " set status = 'READY_TO_MERGE' from a \n" +
        " where a.num = 1 and a.no = tmp.no\n" +
        " and tmp.error is null\n" +
        " and tmp.status = 'JUST_INSERTED'"
    );

    exec(
      " insert into client(id, \"name\", surname, patronymic, cia_id, birth_date, current_gender, charm_id, actual)\n" +
        " select tmp.generatedId as id, tmp.name, surname, patronymic, cia_id as ciaId, \n" +
        " to_date(birth, 'yyyy-MM-dd') as birth_date, lower(gender) as current_gender, ch.id as charm_id, 1  as actual \n" +
        " from TMP_CLIENT as tmp join charm ch on tmp.charm = ch.name \n" +
        " where status = 'READY_TO_MERGE'\n" +
        " and ch.actual = 1" +
        " and error is null" +
        " on conflict(cia_id) do update\n" +
        " set name = excluded.name," +
        " surname = excluded.surname,\n" +
        " patronymic = excluded.patronymic,\n" +
        " birth_date = excluded.birth_date,\n" +
        " current_gender = excluded.current_gender,\n" +
        " charm_id = excluded.charm_id\n," +
        " actual = 1"
    );

    exec(
      "insert into client_addr(client, type, street, house, flat, actual)\n" +
        "select c.id, ad.type, ad.street, ad.house, ad.flat, 1 as actual\n" +
        "from client c join TMP_CLIENT tmp on c.cia_id = tmp.cia_id\n" +
        "join TMP_ADDRESS ad on tmp.no = ad.no\n" +
        "where\n" +
        "tmp.error is null\n" +
        "and ad.error is null\n" +
        "and tmp.status = 'READY_TO_MERGE'\n" +
        "on conflict(client, \"type\") do \n" +
        "update set\n" +
        "street = excluded.street,\n" +
        "house = excluded.house,\n" +
        "flat = excluded.flat,\n" +
        "actual = 1"
    );

    exec(
      " insert into client_phone(client, type, number, actual)\n" +
        " select distinct on(c.id, ph.number)\n" +
        " c.id, ph.type, ph.number, 1 as actual from client c join TMP_CLIENT as tmp on c.cia_id = tmp.cia_id \n" +
        " join TMP_PHONE as ph on tmp.cia_id = ph.client\n" +
        " where tmp.no = ph.no\n" +
        " and c.actual = 1\n" +
        " and tmp.error is null\n" +
        " and tmp.status = 'READY_TO_MERGE'\n" +
        " on conflict(client, number) do\n" +
        " update set\n" +
        " actual  = 1"
    );

    exec(
      "update TMP_CLIENT set " +
        " status = 'MERGED'" +
        " where status = 'READY_TO_MERGE'"
    );


  }

}
