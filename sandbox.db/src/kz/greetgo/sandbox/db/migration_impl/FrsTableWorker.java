package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import kz.greetgo.sandbox.db.migration_impl.model.Transaction;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrsTableWorker implements Closeable {
  public Connection connection;
  public int maxBatchSize;

  private PreparedStatement accountPS;
  private PreparedStatement transactionPS;

  private int accountBatchSize;
  private int transactionBatchSize;

  public Runnable execBatch;

  public FrsTableWorker(Connection connection, int maxBatchSize) throws SQLException {
    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    accountPS = this.connection.prepareStatement("");
    transactionPS = this.connection.prepareStatement("");

    execBatch = () -> {
      try {
        if (accountBatchSize > 0) accountPS.executeBatch();

        if (transactionBatchSize > 0) transactionPS.executeBatch();


        if (accountBatchSize + transactionBatchSize > 0) this.connection.commit();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    };
  }

  public void addToBatch(Account account) {

    try {
      accountPS.setString(1, account.type);
      accountPS.setString(2, account.clientId);
      accountPS.setString(3, account.accountNumber);
      accountPS.setString(4, account.registeredAt);

      accountPS.addBatch();
      accountBatchSize++;

      if (accountBatchSize >= maxBatchSize) {
        accountPS.executeBatch();

        connection.commit();
        accountBatchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addToBatch(Transaction transaction) {

    try {
      transactionPS.setString(1, transaction.type);
      transactionPS.setString(2, transaction.money);
      transactionPS.setString(3, transaction.finishedAt);
      transactionPS.setString(4, transaction.transactionType);
      transactionPS.setString(5, transaction.accountNumber);

      transactionPS.addBatch();
      transactionBatchSize++;

      if (transactionBatchSize >= maxBatchSize) {
        transactionPS.executeBatch();

        connection.commit();
        transactionBatchSize = 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {

  }
}
