package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.*;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.*;

import java.io.*;
import java.sql.Connection;
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

  public BeanGetter<MigrationTestDao> migrationTestDao;

//  private GenerateInputFiles fileGenerator;

  private Connection connection;
  private LocalFileWorker localFileWorker;

  private File outErrorFile;
  private ReportXlsx reportXlsx;
  private String tmpClientTableName;
  private String tmpAddrTableName;
  private String tmpPhoneTableName;

  @BeforeMethod
  public void prepareResources() throws Exception {
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

  @AfterMethod
  public void closeResources() throws Exception {
    migrationTestDao.get().dropTmpTables(tmpClientTableName, tmpAddrTableName, tmpPhoneTableName);

    connection.close();
    localFileWorker.close();
    connection = null;
    localFileWorker = null;
  }

  @Test
  public void test_creationOfTmpTables() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    //
    //
    ciaMigrationWorker.createTmpTables();
    //
    //

    markTmpTablesToDrop(ciaMigrationWorker);
  }

  @Test
  public void test_parsingAndInsertionIntoTmpDb() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

    migrationTestDao.get().dropCiaTmpTables();
    migrationTestDao.get().createTmpTables();

    ciaMigrationWorker.tmpClientTable = "tmp_client";
    ciaMigrationWorker.tmpAddrTable = "tmp_addr";
    ciaMigrationWorker.tmpPhoneTable = "tmp_phone";

    //
    //
    ciaMigrationWorker.parseDataAndSaveInTmpDb(ciaMigrationWorker.prepareInFiles());
    //
    //

    List<Client> expectedClients = fileGenerator.getGeneratedClients();
    List<Address> expectedAddresses = fileGenerator.getGeneratedAddresses();
    List<PhoneNumber> expectedPhoneNumbers = fileGenerator.getGeneratedPhoneNumbers();

    markTmpTablesToDrop(ciaMigrationWorker);

    List<Client> actualClients = migrationTestDao.get().loadClientsList(ciaMigrationWorker.tmpClientTable);
    List<Address> actualAddresses = migrationTestDao.get().loadAddressesList(ciaMigrationWorker.tmpAddrTable);
    List<PhoneNumber> actualPhoneNumbers = migrationTestDao.get().loadPhoneNumbersList(ciaMigrationWorker.tmpPhoneTable);

    assertThat(actualClients).isNotNull();
    assertThat(actualClients).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualClients.size(); i++) {
      assertThatAreEqual(actualClients.get(i), expectedClients.get(i));
    }

    assertThat(actualAddresses).isNotNull();
    assertThat(actualAddresses.size()).isEqualTo(expectedAddresses.size());
    for (int i = 0; i < actualAddresses.size(); i++) {
      assertThatAreEqual(actualAddresses.get(i), expectedAddresses.get(i));
    }

    assertThat(actualPhoneNumbers).isNotNull();
    assertThat(actualPhoneNumbers.size()).isEqualTo(expectedPhoneNumbers.size());
    // sorting because generator shuffles tags
    actualPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number));
    expectedPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number));
    for (int i = 0; i < actualPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void testCiaMigrationByGoodClientsCount() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

    //
    //
    migration.get().executeCiaMigration();
//    migration.get().executeFrsMigration();
    //
    //

    long clientCount = clientTestDao.get().getClientCount();

    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
//    assertThat(clientCount).isEqualTo(699_633);
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
//    assertThat(clientCount).isEqualTo(699_633);
  }

  @Test
  public void testAllGoodClientsInserted() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

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

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

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

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

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

      assertThat(clientAccountNumber).isEqualTo(accountEntry.getValue().accountNumber);
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

  private CiaMigrationWorker getCiaMigrationWorker() {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection, localFileWorker);
    ciaMigrationWorker.outErrorFile = outErrorFile;
    ciaMigrationWorker.reportXlsx = reportXlsx;
    ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return ciaMigrationWorker;
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

  private void assertThatAreEqual(PhoneNumber actualPhoneNumber, PhoneNumber expectedPhoneNumber) {
    assertThat(actualPhoneNumber.type).isEqualTo(expectedPhoneNumber.type);
    assertThat(actualPhoneNumber.number).isEqualTo(expectedPhoneNumber.number);
  }

  private void assertThatAreEqual(Address actualAddress, Address expectedAddress) {
    assertThat(actualAddress.type).isEqualTo(expectedAddress.type);
    assertThat(actualAddress.street).isEqualTo(expectedAddress.street);
    assertThat(actualAddress.house).isEqualTo(expectedAddress.house);
    assertThat(actualAddress.flat).isEqualTo(expectedAddress.flat);
  }

  private void assertThatAreEqual(Client actualClient, Client expectedClient) {
    assertThat(actualClient.cia_id).isEqualTo(expectedClient.cia_id);
    assertThat(actualClient.name).isEqualTo(expectedClient.name);
    assertThat(actualClient.surname).isEqualTo(expectedClient.surname);
    assertThat(actualClient.patronymic).isEqualTo(expectedClient.patronymic);
    assertThat(actualClient.gender).isEqualTo(expectedClient.gender);
    assertThat(actualClient.dateOfBirth).isEqualTo(expectedClient.dateOfBirth);
    assertThat(actualClient.charmName).isEqualTo(expectedClient.charmName);
  }

  private GenerateInputFiles prepareInputFiles(int ciaLimit, int frsLimit) throws Exception {
    GenerateInputFiles fileGenerator = new GenerateInputFiles(ciaLimit, frsLimit);
    fileGenerator.setTestMode();
    fileGenerator.execute();
    return fileGenerator;
//    fileGenerator = new GenerateInputFiles(1_000_000, 10_000_000);

//    fileGenerator = new GenerateInputFiles(50_000, 50);

//    fileGenerator = new GenerateInputFiles(500, 500);
//    fileGenerator.setTestMode();
//    fileGenerator.execute();
//
//    migration.get().setSshMode(false);
  }

  private void markTmpTablesToDrop(CiaMigrationWorker ciaMigrationWorker) {
    tmpClientTableName = ciaMigrationWorker.tmpClientTable;
    tmpAddrTableName = ciaMigrationWorker.tmpAddrTable;
    tmpPhoneTableName = ciaMigrationWorker.tmpPhoneTable;
  }
}