package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import kz.greetgo.sandbox.db.migration_impl.model.Transaction;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link FrsMigrationWorker}
 */
public class FrsMigrationWorkerTest extends ParentTestNg {
  public BeanGetter<Migration> migration;
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  public BeanGetter<MigrationTestDao> migrationTestDao;

//  private GenerateInputFiles fileGenerator;

  private Connection connection;
  private LocalFileWorker localFileWorker;

  private File outErrorFile;
  private ReportXlsx reportXlsx;
  private GenerateInputFiles fileGenerator;

  @BeforeClass
  public void prepareResources() throws Exception {
    fileGenerator = prepareInputFiles(0, 100);
    connection = migration.get().getConnection();
    migration.get().setSshMode(false);
    localFileWorker = (LocalFileWorker) migration.get().getInputFileWorker();
    localFileWorker.homePath = "build/out_files/";

    outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
    outErrorFile.getParentFile().mkdirs();
    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();
  }

  @AfterClass
  public void closeResources() throws Exception {
    connection.close();
    localFileWorker.close();
    connection = null;
    localFileWorker = null;
  }

  @Test
  public void test_creationOfTmpTables() throws Exception {
    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();

    //
    //
    frsMigrationWorker.createTmpTables();
    //
    //
  }

  @Test
  public void test_parsingAndInsertionIntoTmpDb() throws Exception {
    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();

    fileGenerator = prepareInputFiles(0, 100);

    frsMigrationWorker.inputStream = new FileInputStream(fileGenerator.getOutFrsFileName());

    frsMigrationWorker.createTmpTables();

    //
    //
    frsMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<Account> expectedAccounts = fileGenerator.getGeneratedAccounts();
    List<Transaction> expectedTransactions = fileGenerator.getGeneratedTransactions();

    List<Account> actualAccounts = migrationTestDao.get().loadAccountsList(frsMigrationWorker.tmpAccountTable);
    List<Transaction> actualTransactions = migrationTestDao.get().loadTransactionsList(frsMigrationWorker.tmpTransactionTable);

//    expectedAccounts.sort(Comparator.comparing(account -> account.account_number));
    expectedTransactions.sort(Comparator.comparing(transaction -> transaction.finishedAt));

    assertThat(actualAccounts).isNotNull();
    assertThat(actualAccounts).hasSameSizeAs(expectedAccounts);
    for (int i = 0; i < actualAccounts.size(); i++) {
      assertThatAreEqual(actualAccounts.get(i), expectedAccounts.get(i));
    }

    assertThat(actualTransactions).isNotNull();
    assertThat(actualTransactions).hasSameSizeAs(expectedTransactions);
    for (int i = 0; i < actualTransactions.size(); i++) {
      assertThatAreEqual(actualTransactions.get(i), expectedTransactions.get(i));
    }
  }

  private void assertThatAreEqual(Account actual, Account expected) {
    assertThat(actual.account_number).isEqualTo(expected.account_number);
    assertThat(actual.registeredAtD).isEqualTo(expected.registeredAtD);
  }

  @Test
  public void testFrsMigrationByRecordsCount() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(10, 100);

    //
    //
    migration.get().executeFrsMigration();
    //
    //

    long transactionCount = clientTestDao.get().getTransactionCount();
    long accountCount = clientTestDao.get().getAccountCount();

    assertThat(transactionCount).isEqualTo(fileGenerator.getTransactionCount());
    assertThat(accountCount).isEqualTo(fileGenerator.getAccountCount());
  }

  @Test
  public void testAccountInsertion() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(10, 100);

    Map<String, Account> clientAccounts = fileGenerator.getClientAccounts();

    //
    //
    migration.get().executeFrsMigration();
    //
    //


    int i = 0;
    for (Map.Entry<String, Account> accountEntry : clientAccounts.entrySet()) {
      String clientAccountNumber = clientTestDao.get().getClientAccountByCiaId(
        accountEntry.getKey(),
        accountEntry.getValue().registeredAtD
      );

      assertThat(clientAccountNumber).isEqualTo(accountEntry.getValue().account_number);
      if (++i > 10) break;
    }
  }

  @Test
  public void testTransactionInsertion() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(10, 100);

    Map<String, Transaction> accountTransactions = fileGenerator.getAccountTransactions();

    //
    //
    migration.get().executeFrsMigration();
    //
    //


    int i = 0;
    for (Map.Entry<String, Transaction> transactionEntry : accountTransactions.entrySet()) {
      Transaction accountTransactionActual = clientTestDao.get().getTransactionByAccountNumber(
        transactionEntry.getValue().account_number,
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(transactionEntry.getValue().finishedAt));

      assertThat(accountTransactionActual).isNotNull();
      assertThatAreEqual(accountTransactionActual, transactionEntry.getValue());
      if (++i > 10) break;
    }
  }

  @Test
  public void testCiaAndFrsConcurrentlyMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(10, 100);

    //
    //
    migration.get().executeMigration();
    //
    //

    long clientCount = clientTestDao.get().getClientCount();

    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
  }

  private FrsMigrationWorker getFrsMigrationWorker() {
    FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection);
//    frsMigrationWorker.outError = outError;
    frsMigrationWorker.reportXlsx = reportXlsx;
    frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return frsMigrationWorker;
  }

  private void assertThatAreEqual(Transaction actual, Transaction expected) throws ParseException {
    assertThat(actual.money.compareTo(expected.money)).isEqualTo(0);
    assertThat(actual.transaction_type).isEqualTo(String.valueOf(expected.transaction_type));
  }

  private int getLineCountOfFile(String fileName) {
    try
      (
        FileReader input = new FileReader(fileName);
        LineNumberReader count = new LineNumberReader(input);
      ) {
      while (count.skip(Long.MAX_VALUE) > 0) {}
      return count.getLineNumber();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  private GenerateInputFiles prepareInputFiles(int ciaLimit, int frsLimit) throws Exception {
    GenerateInputFiles fileGenerator = new GenerateInputFiles(ciaLimit, frsLimit);
    fileGenerator.setTestMode();
    fileGenerator.execute();
    return fileGenerator;
  }
}