package kz.greetgo.sandbox.db.register_impl.migration;

import com.google.common.io.Files;
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
import java.nio.charset.Charset;
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


    migrationTestDao.get().dropTables(migration.accountTable);
    migrationTestDao.get().dropTables(migration.transactionTable);

  }

  @Test
  public void uploadFilesToTmpTables_validJson() throws Exception {

    MigrationFrs migrationFrs = new MigrationFrs();
    migrationFrs.connection = connection;
    migrationFrs.inFile = createInFile("json_test.json_row");
    migrationFrs.errorsFile = setErrorFile();

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

    migrationTestDao.get().dropTables(migrationFrs.accountTable);
    migrationTestDao.get().dropTables(migrationFrs.transactionTable);

    assertThat(accounts).hasSize(1);
    assertThat(transactions).hasSize(2);

    assertThat(accounts.get(0).client_id).isEqualTo(ciaId);
    assertThat(accounts.get(0).account_number).isEqualTo(accNum);
    assertThat(accounts.get(0).registered_at).isEqualTo("2011-01-23T23:22:11.456");

    assertThat(transactions.get(0).account_number).isEqualTo(accNum);
    assertThat(transactions.get(0).finished_at).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(0).money).isEqualTo("+123_000_000_098.13");
    assertThat(transactions.get(0).transaction_type).isEqualTo("Перечисление с госбюджета");

    assertThat(transactions.get(1).account_number).isEqualTo(accNum);
    assertThat(transactions.get(1).finished_at).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(1).money).isEqualTo("-23_000_000_034.17");
    assertThat(transactions.get(1).transaction_type).isEqualTo("Вывод средств в офшоры");


  }

  @Test
  public void uploadFilesToTmpTables_invalidJson() throws Exception {

    MigrationFrs migrationFrs = new MigrationFrs();
    migrationFrs.connection = connection;
    migrationFrs.inFile = createInFile("json_test_invalid.json_row");

    migrationFrs.errorsFile = setErrorFile();

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


    migrationTestDao.get().dropTables(migrationFrs.accountTable);
    migrationTestDao.get().dropTables(migrationFrs.transactionTable);

    assertThat(accounts).hasSize(0);
    assertThat(transactions).hasSize(2);

    assertThat(transactions.get(0).account_number).isEqualTo(accNum);
    assertThat(transactions.get(0).finished_at).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(0).money).isEqualTo("+123_000_000_098.13");
    assertThat(transactions.get(0).transaction_type).isEqualTo("Перечисление с госбюджета");

    assertThat(transactions.get(1).account_number).isEqualTo(accNum);
    assertThat(transactions.get(1).finished_at).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(transactions.get(1).money).isEqualTo("-23_000_000_034.17");
    assertThat(transactions.get(1).transaction_type).isEqualTo("Вывод средств в офшоры");

    String firstLine = Files.asCharSource(migrationFrs.errorsFile, Charset.defaultCharset()).readFirstLine();

    assertThat(firstLine).isEqualTo("Invalid JSON on line: 2");

  }

  private File setErrorFile() {
    File ret = new File("build/error-" + RND.intStr(8) + ".log");
    ret.getParentFile().mkdirs();
    return ret;
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