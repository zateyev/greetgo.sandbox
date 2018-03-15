package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.Account;
import kz.greetgo.sandbox.db.migration_impl.model.Transaction;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link AbstractMigrationWorker}
 */
public class AbstractMigrationWorkerTest extends ParentTestNg {

  public BeanGetter<Migration> migration;
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  private GenerateInputFiles fileGenerator;

  @BeforeClass
  @BeforeMethod
  public void prepareInputFiles() throws Exception {
//    fileGenerator = new GenerateInputFiles(10_000, 50);
    fileGenerator = new GenerateInputFiles(500, 50);
    fileGenerator.setTestMode();

//    fileGenerator = new GenerateInputFiles(1_000_000, 10_000_000);

    fileGenerator.execute();
    migration.get().setSshMode(false);
  }

  @Test
  public void testCiaMigrationByGoodClientsCount() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    migration.get().executeCiaMigration();
//    migration.get().executeFrsMigration();
    //
    //

    long clientCount = clientTestDao.get().getClientCount();

    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
//    assertThat(clientCount).isEqualTo(699_683);
  }

  @Test
  public void testAllGoodClientsInserted() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    Set<String> goodClientIds = fileGenerator.getGoodClientIds();

    //
    //
    migration.get().executeCiaMigration();
    //
    //

    Set<String> clientCiaIds = clientTestDao.get().getClientCiaIdsSet();


    assertThat(Objects.equals(clientCiaIds, goodClientIds)).isTrue();
  }

  @Test
  public void testHandlingDuplicates() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    Map<String, ClientDetails> lastGoodClientsFromDuplicates = fileGenerator.getLastGoodClients();


    //
    //
    migration.get().executeCiaMigration();
    //
    //

    int i = 0;
    for (Map.Entry<String, ClientDetails> lastClientEntry : lastGoodClientsFromDuplicates.entrySet()) {
      ClientDetails actualClientDetails = clientTestDao.get().getClientDetailsByCiaId(lastClientEntry.getKey());
      actualClientDetails.addressF = clientTestDao.get().selectAddrByClientId(actualClientDetails.id, AddressType.FACT);
      actualClientDetails.addressR = clientTestDao.get().selectAddrByClientId(actualClientDetails.id, AddressType.REG);
      actualClientDetails.phoneNumbers = clientTestDao.get().getPhonesByClientId(actualClientDetails.id);

      assertThatAreEqual(actualClientDetails, lastClientEntry.getValue());
      if (++i > 10) break;
    }
  }

  @Test
  public void testErrorHandling() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    migration.get().executeCiaMigration();
    //
    //

    assertThat(getLineCountOfFile(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName()))
      .isEqualTo(fileGenerator.getErrorRecordCount());
  }

  @Test
  public void testFrsMigrationByRecordsCount() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

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

      assertThat(clientAccountNumber).isEqualTo(accountEntry.getValue().accountNumber);
      if (++i > 10) break;
    }
  }

  @Test
  public void testTransactionInsertion() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    Map<String, Transaction> accountTransactions = fileGenerator.getAccountTransactions();

    //
    //
    migration.get().executeFrsMigration();
    //
    //


    int i = 0;
    for (Map.Entry<String, Transaction> transactionEntry : accountTransactions.entrySet()) {
      Transaction accountTransactionActual = clientTestDao.get().getTransactionByAccountNumber(
        transactionEntry.getValue().accountNumber,
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(transactionEntry.getValue().finishedAt));

      assertThat(accountTransactionActual).isNotNull();
      assertThatAreEqual(accountTransactionActual, transactionEntry.getValue());
      if (++i > 10) break;
    }
  }

  private void assertThatAreEqual(Transaction actual, Transaction expected) throws ParseException {
    assertThat(actual.money.compareTo(expected.money)).isEqualTo(0);
    assertThat(actual.transactionType).isEqualTo(String.valueOf(expected.transactionType));
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

  private void assertThatAreEqual(ClientDetails actual, ClientDetails expected) {
    assertThat(actual.surname).isEqualTo(expected.surname);
    assertThat(actual.name).isEqualTo(expected.name);
    assertThat(actual.patronymic).isEqualTo(expected.patronymic);
    assertThat(actual.gender).isEqualTo(expected.gender);
    assertThat(actual.dateOfBirth).isEqualTo(expected.dateOfBirth);
    assertThat(actual.charm.name).isEqualTo(expected.charm.name);
    assertThat(actual.addressF.street).isEqualTo(expected.addressF.street);
    assertThat(actual.addressF.house).isEqualTo(expected.addressF.house);
    assertThat(actual.addressF.flat).isEqualTo(expected.addressF.flat);
    assertThat(actual.addressR.street).isEqualTo(expected.addressR.street);
    assertThat(actual.addressR.house).isEqualTo(expected.addressR.house);
    assertThat(actual.addressR.flat).isEqualTo(expected.addressR.flat);
    actual.phoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number.toLowerCase()));
    expected.phoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number.toLowerCase()));
    assertThat(actual.phoneNumbers).hasSize(expected.phoneNumbers.size());
    for (int i = 0; i < actual.phoneNumbers.size(); i++) {
      assertThat(actual.phoneNumbers.get(i).number).isEqualTo(expected.phoneNumbers.get(i).number);
      assertThat(actual.phoneNumbers.get(i).phoneType).isEqualTo(expected.phoneNumbers.get(i).phoneType);
    }
  }
}