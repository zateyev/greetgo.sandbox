package kz.greetgo.sandbox.db.register_impl.migration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrsParser implements AutoCloseable {

  private Connection connection;
  private final int maxBatchSize;
  private PreparedStatement accountPS, transactionPS;

  public FrsParser(Connection connection,
                   int maxBatchSize,
                   String accountTable,
                   String transactionTable) throws SQLException {

    this.connection = connection;
    this.maxBatchSize = maxBatchSize;
    connection.setAutoCommit(false);

    accountPS = connection.prepareStatement(
      "insert into " + accountTable + " (cia_id, account_number, registered_at) " +
        " VALUES (?, ?, ?)"
    );


    transactionPS = connection.prepareStatement(
      "insert into " + transactionTable + " (money, finished_at, transaction_type, account_number) " +
        " values (?, ?, ?, ?)"
    );

  }

  int batchSize = 0;
  int recordsCount = 0;

  Account account = new Account();
  Transaction transaction = new Transaction();

  public void parseAndAddBatch(String jsonLine) throws Exception {

    JsonFactory jFactory = new JsonFactory();

    try (JsonParser jPars = jFactory.createParser(jsonLine)) {

      while (jPars.nextToken() != null) {

        String fieldName = jPars.getCurrentName();
        String value = jPars.getText();

        if ("type".equals(fieldName)) {

          if ("transaction".equals(value)) {

            ObjectMapper obj = new ObjectMapper();
            transaction = obj.readValue(jsonLine, Transaction.class);
            addTransactionBatch();
            transaction = new Transaction();

          }

          if ("new_account".equals(value)) {

            ObjectMapper obj = new ObjectMapper();
            account = obj.readValue(jsonLine, Account.class);
            addAccountBatch();
            account = new Account();

          }

        }

      }

    }catch (Exception e){
      System.out.println(e);
    }

  }


  private void addAccountBatch() throws SQLException {
    accountPS.setString(1, account.client_id);
    accountPS.setString(2, account.account_number);
    accountPS.setString(3, account.registered_at);
    accountPS.addBatch();
    executeBatch();
  }

  private void addTransactionBatch() throws SQLException {
    transactionPS.setString(1, transaction.money);
    transactionPS.setString(2, transaction.finished_at);
    transactionPS.setString(3, transaction.transaction_type);
    transactionPS.setString(4, transaction.account_number);
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
