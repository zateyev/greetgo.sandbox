package kz.greetgo.sandbox.db.register_impl.migration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrsHandler implements AutoCloseable {

  private File inFile;
  private Connection connection;
  private final int maxBatchSize;
  private PreparedStatement accountPS, transactionPS;

  public FrsHandler(Connection connection,
                    File inFile,
                    int maxBatchSize,
                    String accountTable,
                    String transactionTable) throws SQLException {

    this.inFile = inFile;
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

  public void parse() throws Exception {

    JsonFactory jFactory = new JsonFactory();

    try (JsonParser jPars = jFactory.createParser(inFile)) {

      while (jPars.nextToken() != null) {

        String fieldName = jPars.getCurrentName();
        String value = jPars.getText();

        if ("type".equals(fieldName)) {

          if ("transaction".equals(value)) {

            jPars.nextToken();
            jPars.nextToken();
            if ("money".equals(jPars.getCurrentName())) transaction.money = jPars.getText();

            jPars.nextToken();
            jPars.nextToken();
            if ("finished_at".equals(jPars.getCurrentName())) transaction.finishedAt = jPars.getText();

            jPars.nextToken();
            jPars.nextToken();
            if ("transaction_type".equals(jPars.getCurrentName())) transaction.type = jPars.getText();


            jPars.nextToken();
            jPars.nextToken();
            if ("account_number".equals(jPars.getCurrentName())) transaction.accountNumber = jPars.getText();

            addTransactionBatch();
            transaction = new Transaction();

          }

          if ("new_account".equals(value)) {

            jPars.nextToken();
            jPars.nextToken();
            if ("client_id".equals(jPars.getCurrentName())) account.ciaId = jPars.getText();

            jPars.nextToken();
            jPars.nextToken();
            if ("account_number".equals(jPars.getCurrentName())) account.number = jPars.getText();

            jPars.nextToken();
            jPars.nextToken();
            if ("registered_at".equals(jPars.getCurrentName())) account.registeredAt = jPars.getText();

            addAccountBatch();
            account = new Account();

          }

        }

      }

    }

  }


  private void addAccountBatch() throws SQLException {
    accountPS.setString(1, account.ciaId);
    accountPS.setString(2, account.number);
    accountPS.setString(3, account.registeredAt);
    accountPS.addBatch();
    executeBatch();
  }

  private void addTransactionBatch() throws SQLException {
    transactionPS.setString(1, transaction.money);
    transactionPS.setString(2, transaction.finishedAt);
    transactionPS.setString(3, transaction.type);
    transactionPS.setString(4, transaction.accountNumber);
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
