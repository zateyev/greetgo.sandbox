package kz.greetgo.sandbox.db.register_impl.migration;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class MigrationFrs {

  public File inFile, errorsFile;
  public Connection connection;
  public int maxBatchSize = 5000;

  private final Logger logger = Logger.getLogger(getClass());

  private void exec(String sql) throws SQLException {

    sql = sql.replace("TMP_ACCOUNTS", accountTable);
    sql = sql.replace("TMP_TRANSACTIONS", transactionTable);

    try (Statement statement = connection.createStatement()) {
      long startedAt = System.nanoTime();
      statement.execute(sql);
      logger.trace("SQL [" + (System.nanoTime() - startedAt) + "] " + sql);
    }

  }

  String accountTable, transactionTable;

  public void migrate() throws Exception {
    createTempTables();
    uploadFileToTempTables();
    mainMigrationOperation();
    downloadErrors();
  }

  void createTempTables() throws SQLException {

    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
    accountTable = "tmp_accounts_" + date;
    transactionTable = "tmp_transactions_" + date;

    exec(
      "create table " + accountTable + "(" +
        " cia_id varchar(50)," +
        " account_number varchar(50)," +
        " registered_at varchar(100)," +
        " generatedId varchar(50)," +
        " status varchar(50) default 'JUST_INSERTED'," +
        " no bigint" +
        ")"
    );

    exec(
      "create table " + transactionTable + "(" +
        " money varchar(100)," +
        " finished_at varchar(100)," +
        " transaction_type varchar(300)," +
        " account_number varchar(50)," +
        " generatedId varchar(50)," +
        " status varchar(100) default 'JUST_INSERTED'," +
        " error varchar(100) default null" +
        ")"
    );

  }

  void uploadFileToTempTables() throws Exception {

    try (FrsParser handler = new FrsParser(
      connection,
      maxBatchSize,
      accountTable,
      transactionTable
    )) {

      handler.errorLog = errorsFile;

      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"))) {
        while (true) {

          String line = br.readLine();

          if (line == null) break;

          handler.parseAndAddBatch(line);

        }
      }
    }

  }

  void mainMigrationOperation() throws SQLException {

    exec(
      "with a as(\n" +
        "SELECT\n" +
        " row_number() over(partition by account_number order by no desc) as num,\n" +
        " * FROM TMP_ACCOUNTS\n" +
        " )\n" +
        " update TMP_ACCOUNTS as tmp\n" +
        " set status = 'READY_TO_MERGE' from a \n" +
        " where a.num = 1 and a.no = tmp.no\n" +
        " and tmp.status = 'JUST_INSERTED'"
    );

    exec(
      "insert into client_account(id, client, \"money\", number, registered_at, actual) \n" +
        "select tmp.generatedId as id, \n" +
        "c.id as client, \n" +
        "0 as \"money\",\n" +
        "account_number as number,\n" +
        "to_timestamp(registered_at, 'yyyy-MM-dd\"T\"HH24:MI:SS.MS') as registered_at, \n" +
        "1 as actual from TMP_ACCOUNTS tmp\n" +
        "join client c on c.cia_id = tmp.cia_id\n" +
        "where tmp.status = 'READY_TO_MERGE'" +
        "on conflict(number)\n" +
        "do update \n" +
        "set client = excluded.client,\n" +
        "registered_at = excluded.registered_at,\n" +
        "actual = 1"
    );

    exec(
      "update TMP_TRANSACTIONS tmptr" +
        " set error = 'Account for this transaction not found'" +
        " from (" +
        " select account_number from TMP_TRANSACTIONS tmptr except select number from client_account" +
        " ) as a" +
        " where tmptr.account_number = a.account_number"
    );

    exec(
      "insert into transaction_type(id, name, actual)\n" +
        " select tmp.generatedId as id, \n" +
        " transaction_type as name, 1 as actual \n" +
        " from TMP_TRANSACTIONS as tmp\n" +
        " where tmp.transaction_type not in (select name from transaction_type where actual = 1)\n" +
        " and tmp.error is null" +
        " group by transaction_type, generatedId limit 1"
    );

    exec(
      "  insert into client_account_transaction(id, account, \"money\", finished_at, \"type\", account_number, actual)\n" +
        "   select tmp.generatedid as id,\n" +
        "   ca.id as account,\n" +
        "   cast(replace(tmp.\"money\", '_', '') as float) as \"money\",\n" +
        "   to_timestamp(tmp.finished_at, 'yyyy-MM-dd\"T\"HH24:MI:SS.MS') as finished_at,\n" +
        "   ttype.id as \"type\",\n" +
        "   tmp.account_number as account_number,\n" +
        "   1 as actual\n" +
        "   from TMP_TRANSACTIONS as tmp\n" +
        "   join transaction_type as ttype on tmp.transaction_type = ttype.\"name\"\n" +
        "   join client_account ca on ca.\"number\" = tmp.account_number\n" +
        "   where ttype.actual = 1" +
        "   and ca.actual = 1" +
        "   and tmp.error is null" +
        "  on conflict(\"money\", finished_at, account_number) do update \n" +
        "  set \"type\" = excluded.type,\n" +
        "  actual = 1"
    );

    exec(
      "update TMP_TRANSACTIONS set status = 'MERGED'" +
        " where status = 'JUST_INSERTED' and error is null"
    );

    exec(
      "with a as (select tr.account, sum(tr.\"money\")\n" +
        "from client_account_transaction as tr join client_account\n" +
        "on tr.account = client_account.id\n" +
        "where tr.actual = 1 group by account)\n" +
        "update client_account set \"money\" = \"money\" + a.sum\n" +
        "from a\n" +
        "where a.account = id"
    );

    exec(
      "update client_account_transaction set actual = 0\n" +
        "where actual = 1"
    );




  }

  void downloadErrors() throws Exception {

    try (FileWriter out = new FileWriter(errorsFile, true)) {

      TransactionErrorWriter ew = new TransactionErrorWriter(
        out, connection, transactionTable
      );

    }


  }

}
