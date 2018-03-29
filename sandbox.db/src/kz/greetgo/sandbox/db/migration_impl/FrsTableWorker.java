package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.AccountTmp;
import kz.greetgo.sandbox.db.migration_impl.model.TransactionTmp;

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

  public FrsTableWorker(Connection connection, int maxBatchSize, String accountTable, String transactionTable) throws SQLException {
    this.connection = connection;
    this.maxBatchSize = maxBatchSize;

    accountPS = this.connection.prepareStatement(
      "INSERT INTO " + accountTable + " (type, client_id, account_number, registered_at) " +
        "VALUES (?, ?, ?, ?)");

    transactionPS = this.connection.prepareStatement(
      "INSERT INTO " + transactionTable + " (type, money, finished_at, transaction_type, account_number) " +
        "VALUES (?, ?, ?, ?, ?)");
  }

  public void addToBatch(AccountTmp account) {

    try {
      int ind = 1;
      accountPS.setString(ind++, account.type);
      accountPS.setString(ind++, account.clientId);
      accountPS.setString(ind++, account.account_number);
      accountPS.setTimestamp(ind, new Timestamp(SDF.parse(account.registeredAt).getTime()));

      accountPS.addBatch();
      accountBatchSize++;

      if (accountBatchSize >= maxBatchSize) {
        accountPS.executeBatch();

        connection.commit();
        accountBatchSize = 0;
      }
    } catch (SQLException | ParseException e) {
      e.printStackTrace();
    }
  }

  public void addToBatch(TransactionTmp transaction) {

    try {
      int ind = 1;
      transactionPS.setString(ind++, transaction.type);
      transactionPS.setBigDecimal(ind++, transaction.money);
      transactionPS.setTimestamp(ind++, new Timestamp(SDF.parse(transaction.finishedAt).getTime()));
      transactionPS.setString(ind++, transaction.transaction_type);
      transactionPS.setString(ind, transaction.account_number);

      transactionPS.addBatch();
      transactionBatchSize++;

      if (transactionBatchSize >= maxBatchSize) {
        transactionPS.executeBatch();

        connection.commit();
        transactionBatchSize = 0;
      }
    } catch (SQLException | ParseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (accountBatchSize > 0) accountPS.executeBatch();
      if (transactionBatchSize > 0) transactionPS.executeBatch();
      if (accountBatchSize + transactionBatchSize > 0) this.connection.commit();

      accountPS.close();
      transactionPS.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
