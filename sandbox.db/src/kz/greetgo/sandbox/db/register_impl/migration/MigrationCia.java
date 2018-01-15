package kz.greetgo.sandbox.db.register_impl.migration;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class MigrationCia {
  public File inFile, errorsFile;
  public Connection connection;
  public int maxBatchSize = 5000;

  private final Logger logger = Logger.getLogger(getClass());

  private void exec(String sql) throws SQLException {

    sql = sql.replaceAll("TMP_CLIENT", clientTable);
    sql = sql.replaceAll("TMP_ADDRESS", addressTable);
    sql = sql.replaceAll("TMP_PHONE", phoneTable);

    try (Statement statement = connection.createStatement()) {
      long startedAt = System.nanoTime();
      statement.execute(sql);
      logger.trace("SQL [" + (System.nanoTime() - startedAt) + "] " + sql);
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

  void createTempTables() throws Exception {
    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

    clientTable = "tmp_client_" + date;
    addressTable = "tmp_client_address_" + date;
    phoneTable = "tmp_client_phone_" + date;

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
        " number varchar(50), " +

        " primary key(no, number)" +
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
        errorLog.append("SAX Parse Error: " + e.getMessage() + "\n");
        errorLog.append("At line [" + e.getLineNumber() + "] " + "Column number: [" + e.getColumnNumber() + "]\n");
      }
    }
  }

  void downloadErrors() throws IOException {

    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

    errorsFile = new File("build/errorsFile_" + date + ".log");
    errorsFile.getParentFile().mkdirs();

    FileWriter out = new FileWriter(errorsFile);
    out.write(errorLog.toString());
    out.close();

  }

  void mainMigrationOperation() throws SQLException {

    exec(
      "update TMP_CLIENT set\n" +
        "error = case\n" +
        "when \"name\" is null\n" +
        "then '[Name is null] Client fields cannot be null'\n" +
        "when surname is null\n" +
        "then '[Surname is null] Client fields cannot be null'\n" +
        "when gender is null\n" +
        "then '[Gender is null] Client fields cannot be null'\n" +
        "when birth is null\n" +
        "then '[Birth date is null] Client fields cannot be null'\n" +
        "when charm is null\n" +
        "then '[Charm date is null] Client fields cannot be null'\n" +
        "end\n" +
        "where \n" +
        "name is null \n" +
        "or surname is null \n" +
        "or birth is null\n" +
        "or gender is null\n" +
        "or charm is null"
    );

    exec(
      "update TMP_CLIENT as ad\n" +
        "set error = case \n" +
        "when ad.street is null \n" +
        "then '[Street is null] Address fields cannot be null'\n" +
        "when ad.house is null \n" +
        "then '[House is null] Address fields cannot be null'\n" +
        "when ad.flat is null \n" +
        "then '[Flat is null] Address fields cannot be null'\n" +
        "end\n" +
        "from  (select client, no from TMP_CLIENT where \n" +
        "street is null \n" +
        "or house is null\n" +
        "or flat is null \n" +
        "group by client, \"no\") as a\n" +
        "where ad.client  = a.client\n" +
        "and ad.no = a.no"
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
        " select  charm, tmp.generatedId as id, 1 as actual \n" +
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
      "insert into client_phone(client, type, number, actual)\n" +
        " select c.id, ph.type, ph.number, 1 as actual from client c join TMP_CLIENT as tmp on c.cia_id = tmp.cia_id \n" +
        " join TMP_PHONE as ph on tmp.cia_id = ph.client\n" +
        " where tmp.\"no\" = ph.\"no\"\n" +
        " and c.actual = 1" +
        " and tmp.error is null" +
        " and tmp.status = 'READY_TO_MERGE'" +
        " on conflict(client, number) do\n" +
        " update set \"number\" = excluded.account_number," +
        " actual  = 1," +
        " type = excluded.transaction_type\n"
    );

    exec(
      "update TMP_CLIENT set " +
        " status = 'MERGED'" +
        " where status = 'READY_TO_MERGE'"
    );


  }

}
