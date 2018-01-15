package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import kz.greetgo.util.ServerUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationFrsTest extends ParentTestNg{

  public BeanGetter<MigrationManager> migrationManager;
  public BeanGetter<MigrationTestDao> migrationTestDao;

  Connection connection;

  @BeforeMethod
  public void createConnection() throws Exception {
    connection = migrationManager.get().createConnection();
  }

  @AfterMethod
  public void closeConnection() throws Exception {
    connection.close();
    connection = null;
  }

  @Test
  public void createTempTables() throws SQLException {

    MigrationFrs migration = new MigrationFrs();
    migration.connection = connection;

    migration.createTempTables();

  }

  @Test
  public void uploadFilesToTmpTables() throws Exception {

    MigrationFrs migrationFrs = new MigrationFrs();
    migrationFrs.connection = connection;
    migrationFrs.inFile = createInFile("json_test.json_row");

    migrationFrs.createTempTables();
    //
    //
    migrationFrs.uploadFileToTempTables();
    //
    //
    migrationFrs.downloadErrors();

    String ciaId = "4-DU8-32-H7";
    String accNum = "32134KZ343-43546-535436-77656";

    List<Account> accounts = migrationTestDao.get().getAccounts(migrationFrs.accountTable, ciaId);
    List<Transaction> transactions = migrationTestDao.get().getTransactions(migrationFrs.transactionTable, accNum);


    assertThat(accounts).hasSize(1);
    assertThat(transactions).hasSize(2);

    assertThat(accounts.get(0).ciaId).isEqualTo(ciaId);
    assertThat(accounts.get(0).number).isEqualTo(accNum);
    assertThat(accounts.get(0).registeredAt).isEqualTo("2011-01-23T23:22:11.456");

    assertThat(transactions.get(0).accountNumber).isEqualTo(accNum);
    assertThat(transactions.get(0).finishedAt).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(0).money).isEqualTo("+123_000_000_098.13");
    assertThat(transactions.get(0).type).isEqualTo("Перечисление с госбюджета");

    assertThat(transactions.get(1).accountNumber).isEqualTo(accNum);
    assertThat(transactions.get(1).finishedAt).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(1).money).isEqualTo("-23_000_000_034.17");
    assertThat(transactions.get(1).type).isEqualTo("Вывод средств в офшоры");


  }

  //TODO finish testing invalid
  @Test
  public void uploadFilesToTmpTables_invalidJson() throws Exception {

    MigrationFrs migrationFrs = new MigrationFrs();
    migrationFrs.connection = connection;
    migrationFrs.inFile = createInFile("json_test_invalid.json_row");

    migrationFrs.createTempTables();
    //
    //
    migrationFrs.uploadFileToTempTables();
    //
    //

    String ciaId = "4-DU8-32-H7";
    String accNum = "32134KZ343-43546-535436-77656";

    List<Account> accounts = migrationTestDao.get().getAccounts(migrationFrs.accountTable, ciaId);
    List<Transaction> transactions = migrationTestDao.get().getTransactions(migrationFrs.transactionTable, accNum);


    assertThat(accounts).hasSize(1);
    assertThat(transactions).hasSize(2);

    assertThat(accounts.get(0).ciaId).isEqualTo(ciaId);
    assertThat(accounts.get(0).number).isEqualTo(accNum);
    assertThat(accounts.get(0).registeredAt).isEqualTo("2011-01-23T23:22:11.456");

    assertThat(transactions.get(0).accountNumber).isEqualTo(accNum);
    assertThat(transactions.get(0).finishedAt).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(0).money).isEqualTo("+123_000_000_098.13");
    assertThat(transactions.get(0).type).isEqualTo("Перечисление с госбюджета");

    assertThat(transactions.get(1).accountNumber).isEqualTo(accNum);
    assertThat(transactions.get(1).finishedAt).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(1).money).isEqualTo("-23_000_000_034.17");
    assertThat(transactions.get(1).type).isEqualTo("Вывод средств в офшоры");


  }

  private File createInFile(String resource) throws IOException {
    File ret = new File("build/inFile_" + RND.intStr(10) + "_" + resource);
    ret.getParentFile().mkdirs();
    try (InputStream in = getClass().getResourceAsStream(resource)) {
      try (FileOutputStream out = new FileOutputStream(ret)) {
        ServerUtil.copyStreamsAndCloseIn(in, out);
      }
    }
    return ret;
  }
}