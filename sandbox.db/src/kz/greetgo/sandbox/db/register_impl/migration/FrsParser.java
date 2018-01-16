package kz.greetgo.sandbox.db.register_impl.migration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrsParser implements AutoCloseable {

  private Connection connection;
  private final int maxBatchSize;
  private PreparedStatement accountPS, transactionPS;
  private IdGenerator id = new IdGenerator();

  public File errorLog;

  public FrsParser(Connection connection,
                   int maxBatchSize,
                   String accountTable,
                   String transactionTable) throws SQLException {

    this.connection = connection;
    this.maxBatchSize = maxBatchSize;
    connection.setAutoCommit(false);

    accountPS = connection.prepareStatement(
      "insert into " + accountTable + " (cia_id, account_number, registered_at, generatedId, no) " +
        " VALUES (?, ?, ?, ?, ?)"
    );


    transactionPS = connection.prepareStatement(
      "insert into " + transactionTable + " (money, finished_at, transaction_type, account_number, generatedId) " +
        " values (?, ?, ?, ?, ?)"
    );

  }

  int batchSize = 0;
  int recordsCount = 0;
  long lineNo = 0, no = 0;

  Account account = new Account();
  Transaction transaction = new Transaction();

  public void parseAndAddBatch(String jsonLine) throws Exception {

    JsonFactory jFactory = new JsonFactory();
    lineNo++;

    try (JsonParser jPars = jFactory.createParser(jsonLine)) {

      while ((jPars.nextToken() != null)) {

        String fieldName = jPars.getCurrentName();
        String value = jPars.getValueAsString();

        if ("type".equals(fieldName)) {


          if ("transaction".equals(value)) {

            ObjectMapper obj = new ObjectMapper();

            try {

              transaction = obj.readValue(jsonLine, Transaction.class);
              addTransactionBatch();

            } catch (Exception e) {
              try (FileWriter out = new FileWriter(errorLog, true)) {
                out.write(e.toString());
              }
            }

            transaction = new Transaction();
            continue;

          }

          if ("new_account".equals(value)) {

            ObjectMapper obj = new ObjectMapper();

            try {

              account = obj.readValue(jsonLine, Account.class);
              addAccountBatch();

            } catch (Exception e) {
              try (FileWriter out = new FileWriter(errorLog, true)) {
                out.write(e.toString());
              }
            }
            account = new Account();
            continue;

          }

        }

      }
    } catch (JsonParseException e) {
      try (FileWriter out = new FileWriter(errorLog, true)) {
        out.write("Invalid JSON on line: " + lineNo + "\n");
      }
    }

  }


  private void addAccountBatch() throws SQLException {
    accountPS.setString(1, account.client_id);
    accountPS.setString(2, account.account_number);
    accountPS.setString(3, account.registered_at);
    accountPS.setString(4, id.newId());
    accountPS.setLong(5, no++);
    accountPS.addBatch();
    executeBatch();
  }

  private void addTransactionBatch() throws SQLException {
    transactionPS.setString(1, transaction.money);
    transactionPS.setString(2, transaction.finished_at);
    transactionPS.setString(3, transaction.transaction_type);
    transactionPS.setString(4, transaction.account_number);
    transactionPS.setString(5, id.newId());
    transactionPS.addBatch();
    executeBatch();
  }

  private void executeBatch() throws SQLException {
    recordsCount++;
    batchSize++;
    if (batchSize >= maxBatchSize) {

      accountPS.executeBatch();
      transactionPS.executeBatch();
      connection.commit();
      batchSize = 0;

    }

  }

  @Override
  public void close() throws SQLException {
    if (batchSize > 0) {
      transactionPS.executeBatch();
      accountPS.executeBatch();
      connection.commit();
    }

    transactionPS.close();
    accountPS.close();
    connection.setAutoCommit(true);
  }

}
