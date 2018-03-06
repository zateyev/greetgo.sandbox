package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import kz.greetgo.sandbox.db.migration_impl.model.Transaction;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FrsTableWorker implements Closeable {
  public Connection connection;
  public int maxBatchSize;

  private PreparedStatement accountPS;
  private PreparedStatement transactionPS;

  private int accountBatchSize;
  private int transactionBatchSize;

  private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  public Runnable execBatch;

  public FrsTableWorker(Connection connection, int maxBatchSize) throws SQLException {
    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    accountPS = this.connection.prepareStatement(
      "INSERT INTO TMP_ACCOUNT (type, client_id, account_number, registered_at) VALUES (?, ?, ?, ?)");

    transactionPS = this.connection.prepareStatement(
      "INSERT INTO TMP_TRANSACTION (type, money, finished_at, transaction_type, account_number) VALUES (?, ?, ?, ?, ?)");

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

  public void addToBatch(Account account) throws ParseException {

    try {
      int index = 1;
      accountPS.setString(index++, account.type);
      accountPS.setString(index++, account.clientId);
      accountPS.setString(index++, account.accountNumber);
      accountPS.setTimestamp(index, new Timestamp(SDF.parse(account.registeredAt).getTime()));

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

  public void addToBatch(Transaction transaction) throws ParseException {

    try {
      int index = 1;
      transactionPS.setString(index++, transaction.type);
      transactionPS.setString(index++, transaction.money);
      transactionPS.setTimestamp(index++, new Timestamp(SDF.parse(transaction.finishedAt).getTime()));
      transactionPS.setString(index++, transaction.transactionType);
      transactionPS.setString(index, transaction.accountNumber);

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
