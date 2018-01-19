package kz.greetgo.sandbox.db.register_impl.migration;

import com.google.common.io.Files;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.register_impl.migration.models.AccountTransaction;
import kz.greetgo.sandbox.db.register_impl.migration.models.ClientAccount;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MainMigrationFrsTest extends ParentTestNg {

  public BeanGetter<MigrationManager> migrationManager;
  public BeanGetter<MigrationTestDao> migrationTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;

  Connection connection;
  MigrationFrs migration = new MigrationFrs();
  long no = 0; //Порядковый номер для аккаунта клиента

  public MainMigrationFrsTest() throws FileNotFoundException {}

  @BeforeMethod
  public void createConnection() throws Exception {
    connection = migrationManager.get().createConnection();
    migration.connection = connection;
    migration.createTempTables();
  }


  @AfterMethod
  public void closeConnection() throws Exception {
    migrationTestDao.get().dropTables(migration.accountTable);
    migrationTestDao.get().dropTables(migration.transactionTable);
    connection.close();
    connection = null;
  }

  @Test
  public void mainMigrationsFrs_validData() throws SQLException {

    deleteAll();

    String ciaId1 = RND.str(10);
    String ciaId2 = RND.str(10);

    String accountNum = RND.str(10);

    insertClientWithCiaId(ciaId1);
    insertClientWithCiaId(ciaId2);


    insertAccount(migration.accountTable, ciaId1, accountNum, "2011-01-23T23:22:11.456");
    insertAccount(migration.accountTable, ciaId2, "AccountNumber2", "2012-01-23T23:22:11.456");

    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum);

    insertTransactions(
      migration.transactionTable,
      "-123_015.12",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum);

    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2011-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum);

    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2009-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      "AccountNumber2");


    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      "AccountNumber2");

    //
    //
    migration.mainMigrationOperation();
    //
    //

    ClientAccount account1 = clientTestDao.get().getAccounts(accountNum);
    List<AccountTransaction> transactions = clientTestDao.get().getTransactions(accountNum);
    List<String> transactionType = clientTestDao.get().getTransactionType();
    ClientDetails det = clientTestDao.get().getClientByCiaId(ciaId1);

    assertThat(account1.client).isEqualTo(det.id);
    assertThat(account1.money).isEqualTo(123015.12f);
    assertThat(account1.registered_at.toString()).isEqualTo("Sun Jan 23 23:22:11 ALMT 2011");
    assertThat(transactions).hasSize(3);
    assertThat(transactions.get(0).account_number).isEqualTo(account1.number);
    assertThat(transactions.get(1).account_number).isEqualTo(account1.number);
    assertThat(transactions.get(2).account_number).isEqualTo(account1.number);
    assertThat(transactionType).hasSize(1);
    assertThat(transactionType.get(0)).isEqualTo("Перечисление с госбюджета");

  }

  @Test
  public void mainMigrationsFrs_CheckForMissAccount() throws Exception {
    deleteAll();

    String ciaId1 = RND.str(10);
    String accountNum = RND.str(10);

    insertClientWithCiaId(ciaId1);


    insertAccount(migration.accountTable, ciaId1, accountNum, "2011-01-23T23:22:11.456");

    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2009-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      "AccountNumber222");


    insertTransactions(
      migration.transactionTable,
      "+123_015.12",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      "AccountNumber222");

    migration.errorsFile = new File("build/errorsFRS-" + RND.intStr(5) + ".log");
    //
    //
    migration.mainMigrationOperation();
    migration.downloadErrors();
    //
    //

    ClientAccount account1 = clientTestDao.get().getAccounts(accountNum);
    List<String> transactionType = clientTestDao.get().getTransactionType();
    List<String> allTransactions = clientTestDao.get().getAllTransactions();
    String firstLine = getFirstLine(migration.errorsFile);


    assertThat(account1.money).isEqualTo(0);
    assertThat(account1.registered_at.toString()).isEqualTo("Sun Jan 23 23:22:11 ALMT 2011");
    assertThat(transactionType).hasSize(0);
    assertThat(allTransactions).hasSize(0);
    assertThat(firstLine).contains("Account number is [ AccountNumber222 ] \t Error is [Account for this transaction not found] ");


  }

  @Test
  public void mainMigrationsFrs_RepeatedAccNumber() throws SQLException {

    String ciaId1 = RND.str(10);
    String accountNum = RND.str(10);

    insertClientWithCiaId(ciaId1);


    insertAccount(migration.accountTable, ciaId1, accountNum, "2011-01-23T23:22:11.456");
    insertAccount(migration.accountTable, ciaId1, accountNum, "2012-01-23T23:22:11.456");
    //
    //
    migration.mainMigrationOperation();
    //
    //
    ClientDetails det = clientTestDao.get().getClientByCiaId(ciaId1);


    ClientAccount account1 = clientTestDao.get().getAccounts(accountNum);
    assertThat(account1.registered_at.toString()).isEqualTo("Mon Jan 23 23:22:11 ALMT 2012");
    assertThat(account1.client).isEqualTo(det.id);

  }

  @Test
  public void mainMigrationFRS_CheckForCountingMoney() throws SQLException {

    deleteAll();

    String ciaId1 = RND.str(10);
    String accountNum = RND.str(10);
    String accountNum2 = RND.str(10);

    insertClientWithCiaId(ciaId1);

    insertAccount(migration.accountTable, ciaId1, accountNum, "2012-01-23T23:22:11.456");
    insertAccount(migration.accountTable, ciaId1, accountNum2, "2012-01-23T23:22:11.456");


    insertTransactions(
      migration.transactionTable,
      "+123",
      "2009-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum);


    insertTransactions(
      migration.transactionTable,
      "+123",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum);

    insertTransactions(
      migration.transactionTable,
      "-123",
      "2010-01-23T11:56:11.987",
      "Перечисление с госбюджета",
      accountNum2);

    //
    //
    migration.mainMigrationOperation();
    //
    //

    ClientAccount account1 = clientTestDao.get().getAccounts(accountNum);
    ClientAccount account2 = clientTestDao.get().getAccounts(accountNum2);

    assertThat(account1.money).isEqualTo(246);
    assertThat(account2.money).isEqualTo(-123);


  }


  private void insertAccount(String tableName,
                             String ciaId,
                             String accountNumber,
                             String registeredAt) {

    migrationTestDao.get().insertAccount(
      tableName,
      ciaId,
      accountNumber,
      registeredAt,
      RND.str(15),
      no++
    );

  }

  private void insertTransactions(String tableName,
                                  String money,
                                  String finishedAt,
                                  String transactionType,
                                  String accountNumber) {

    migrationTestDao.get().insertTransaction(
      tableName,
      money,
      finishedAt,
      transactionType,
      accountNumber,
      RND.str(15)

    );

  }

  private String getFirstLine(File errorsFile) throws IOException {
    return Files.asCharSource(errorsFile, Charset.defaultCharset()).readFirstLine();
  }

  private void insertClientWithCiaId(String ciaId) {

    clientTestDao.get().insertClientWithCia(RND.str(10), ciaId);

  }

  private void deleteAll() {
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
    migrationTestDao.get().deleteAllTransactions();
    migrationTestDao.get().deleteAllTransactionType();
  }


  private Map<Object, Map<String, Object>> loadTable(String keyField, String table) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("select * from " + table)) {
      try (ResultSet rs = ps.executeQuery()) {

        List<String> cols = null;

        Map<Object, Map<String, Object>> ret = new HashMap<>();

        while (rs.next()) {

          if (cols == null) {
            cols = new ArrayList<>();
            for (int i = 1, n = rs.getMetaData().getColumnCount(); i <= n; i++) {
              cols.add(rs.getMetaData().getColumnName(i));
            }
          }

          HashMap<String, Object> record = new HashMap<>();
          for (String col : cols) {
            record.put(col, rs.getObject(col));
          }

          ret.put(record.get(keyField), record);

        }

        return ret;

      }
    }
  }


}
