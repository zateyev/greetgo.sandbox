package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneType;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static kz.greetgo.sandbox.db.migration_impl.model.ClientTmp.STATUS_NOT_DUPLICATED;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link CiaMigrationWorker}
 */
public class CiaMigrationWorkerTest extends ParentTestNg {
  public BeanGetter<Migration> migration;
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  public BeanGetter<MigrationTestDao> migrationTestDao;

  private Connection connection;
  private LocalFileWorker localFileWorker;

  private OutputStream outError;
  private ReportXlsx reportXlsx;
  private GenerateInputFiles fileGenerator;

  @BeforeClass
  public void prepareResources() throws Exception {
    fileGenerator = prepareInputFiles(100, 0);
    connection = migration.get().getConnection();
    migration.get().setSshMode(false);
    localFileWorker = (LocalFileWorker) migration.get().getInputFileWorker();
    localFileWorker.homePath = "build/out_files/";

    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();

    File outErrorFile = new File("build/out_files/error_report.txt");
    //noinspection ResultOfMethodCallIgnored
    outErrorFile.getParentFile().mkdirs();
    outError = new FileOutputStream(outErrorFile);
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
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    //
    //
    ciaMigrationWorker.createTmpTables();
    //
    //
  }

  @Test
  public void test_parsingAndInsertionIntoTmpDb() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.inputStream = new FileInputStream(fileGenerator.getOutCiaFileName());

    ciaMigrationWorker.createTmpTables();

    //
    //
    ciaMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<ClientTmp> expectedClients = fileGenerator.getGeneratedClients();
    List<Address> expectedAddresses = fileGenerator.getGeneratedAddresses();
    List<PhoneNumber> expectedPhoneNumbers = fileGenerator.getGeneratedPhoneNumbers();

    List<ClientTmp> actualClients = migrationTestDao.get().loadClientsList(ciaMigrationWorker.tmpClientTable);
    List<Address> actualAddresses = migrationTestDao.get().loadAddressesList(ciaMigrationWorker.tmpAddrTable);
    List<PhoneNumber> actualPhoneNumbers = migrationTestDao.get().loadPhoneNumbersList(ciaMigrationWorker.tmpPhoneTable);

    assertThat(actualClients).isNotNull();
    assertThat(actualClients).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualClients.size(); i++) {
      assertThatAreEqual(actualClients.get(i), expectedClients.get(i));
    }

    assertThat(actualAddresses).isNotNull();
    assertThat(actualAddresses).hasSameSizeAs(expectedAddresses);
    for (int i = 0; i < actualAddresses.size(); i++) {
      assertThatAreEqual(actualAddresses.get(i), expectedAddresses.get(i));
    }

    assertThat(actualPhoneNumbers).isNotNull();
    assertThat(actualPhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    // sorting because generator shuffles tags
    actualPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.phone_number));
    expectedPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.phone_number));
    for (int i = 0; i < actualPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void testValidation() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    List<ClientTmp> generatedClients = fileGenerator.getGeneratedClients();
    List<ClientTmp> expectedErrorClients = fileGenerator.getErrorClients();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < generatedClients.size(); i++) {
      generatedClients.get(i).id = i;
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, generatedClients.get(i));
    }

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualErrorClients = migrationTestDao.get().loadErrorClientsList(ciaMigrationWorker.tmpClientTable);

    assertThat(actualErrorClients).isNotNull();
    assertThat(actualErrorClients).hasSameSizeAs(expectedErrorClients);
    for (int i = 0; i < actualErrorClients.size(); i++) {
      assertThatAreEqual(actualErrorClients.get(i), expectedErrorClients.get(i));
    }
  }


  @Test
  public void validate_cia_id_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname = new ClientTmp();
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(1);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("cia_id");
  }

  @Test
  public void validate_name_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname = new ClientTmp();
    clientNoSurname.cia_id = RND.str(10);
    clientNoSurname.surname = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(1);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("name");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientNoSurname.cia_id);
    assertThat(actualList.get(0).error).doesNotContain("surname");
  }

  @Test
  public void validate_surname_if_client_is_empty() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname = new ClientTmp();
    clientNoSurname.cia_id = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(1);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("surname");
  }


  @Test
  public void test_exclusionOfDuplicateClientRecords() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    List<ClientTmp> generatedClients = fileGenerator.getGeneratedClients();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < generatedClients.size(); i++) {
      if (!generatedClients.get(i).hasError) {
        generatedClients.get(i).id = i;
        migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, generatedClients.get(i));
      }
    }

    Map<String, ClientTmp> uniqueGoodClients = fileGenerator.getUniqueGoodClients();

    //
    //
    ciaMigrationWorker.markDuplicateClientRecords();
    //
    //

    List<ClientTmp> actualUniqueClients = migrationTestDao.get().loadUniqueClientsList(ciaMigrationWorker.tmpClientTable);

    assertThat(actualUniqueClients).isNotNull();
    assertThat(actualUniqueClients).hasSameSizeAs(uniqueGoodClients.entrySet());
    for (ClientTmp actualDuplicateClient : actualUniqueClients) {
      assertThatAreEqual(actualDuplicateClient, uniqueGoodClients.get(actualDuplicateClient.cia_id));
    }
  }

  @Test
  public void исключение_дубликатов() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp client1 = new ClientTmp();
    client1.id = 123;
    client1.cia_id = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client1);

    ClientTmp client2 = new ClientTmp();
    client1.id = 5435;
    client2.cia_id = client1.cia_id;
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client2);

    //
    //
    ciaMigrationWorker.markDuplicateClientRecords();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);

    assertThat(actualList.get(0).id).isEqualTo(123);
    assertThat(actualList.get(1).id).isEqualTo(5435);

    assertThat(actualList.get(0).status).isNotEqualTo(STATUS_NOT_DUPLICATED);
    assertThat(actualList.get(1).status).isEqualTo(STATUS_NOT_DUPLICATED);

  }

  @Test
  public void test_checkingForClientExistence() throws Exception {
    clientTestDao.get().removeAllData();

    Map<String, ClientTmp> uniqueGoodClients = fileGenerator.getUniqueGoodClients();

    List<ClientTmp> expectedClients = new ArrayList<>(uniqueGoodClients.values());
    List<ClientTmp> expectedExistingClients = new ArrayList<>();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < expectedClients.size(); i++) {
      expectedClients.get(i).id = i;
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, expectedClients.get(i));
      if (i % 2 == 0) {
        clientTestDao.get().insertClientM(expectedClients.get(i));
        expectedExistingClients.add(expectedClients.get(i));
      }
    }

    //
    //
    ciaMigrationWorker.checkForClientExistence();
    //
    //

    List<ClientTmp> actualExistingClients = migrationTestDao.get().loadExistingClientsList(ciaMigrationWorker.tmpClientTable);

    assertThat(actualExistingClients).isNotNull();
    assertThat(actualExistingClients).hasSameSizeAs(expectedExistingClients);
    for (int i = 0; i < actualExistingClients.size(); i++) {
      assertThatAreEqual(actualExistingClients.get(i), expectedExistingClients.get(i));
    }
  }

  @Test
  public void test_insertCharms() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    List<ClientTmp> uniqueGoodClients = new ArrayList<>(fileGenerator.getUniqueGoodClients().values());

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();
    Set<String> expectedCharmNames = new HashSet<>();

    for (int i = 0; i < uniqueGoodClients.size(); i++) {
      ClientTmp uniqueGoodClient = uniqueGoodClients.get(i);
      uniqueGoodClient.id = i;
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, uniqueGoodClient);
      expectedCharmNames.add(uniqueGoodClient.charm_name);
    }

    //
    //
    ciaMigrationWorker.insertCharms();
    //
    //

    Set<String> actualCharmNames = charmTestDao.get().loadCharmNamesSet();

    assertThat(actualCharmNames).isEqualTo(expectedCharmNames);
  }

  @Test
  public void test_upsertClients() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    List<ClientTmp> expectedClients = new ArrayList<>(fileGenerator.getUniqueGoodClients().values());

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < expectedClients.size(); i++) {
      expectedClients.get(i).id = i;
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, expectedClients.get(i));
      if (i % 2 == 0) {
        clientTestDao.get().insertClientM(expectedClients.get(i));
      }
    }

    ciaMigrationWorker.checkForClientExistence();
    ciaMigrationWorker.insertCharms();

    //
    //
    ciaMigrationWorker.upsertClients();
    //
    //

    List<ClientTmp> actualUpsertedClients = clientTestDao.get().loadClientList();

    expectedClients.sort((o1, o2) -> o1.surname.compareToIgnoreCase(o2.surname));
    actualUpsertedClients.sort((o1, o2) -> o1.surname.compareToIgnoreCase(o2.surname));

    assertThat(actualUpsertedClients).isNotNull();
    assertThat(actualUpsertedClients).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualUpsertedClients.size(); i++) {
      assertThatAreEqual(actualUpsertedClients.get(i), expectedClients.get(i));
    }
  }

  @Test
  public void test_upsertClientAddress() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    List<Address> expectedAddresses = new ArrayList<>();
    Map<String, ClientDetails> uniqueGoodClientDetails = fileGenerator.getUniqueGoodClientDetails();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    int ind = 0;
    for (Map.Entry<String, ClientDetails> clientDetailsEntry : uniqueGoodClientDetails.entrySet()) {
      ClientTmp client = new ClientTmp();
      client.id = ind;
      client.cia_id = clientDetailsEntry.getKey();
      client.name = clientDetailsEntry.getValue().name;
      client.surname = clientDetailsEntry.getValue().surname;
      client.patronymic = clientDetailsEntry.getValue().patronymic;
      client.gender = clientDetailsEntry.getValue().gender.toString();
      client.birth_date = new SimpleDateFormat("yyyy-MM-dd").parse(clientDetailsEntry.getValue().dateOfBirth);
      client.charm_name = clientDetailsEntry.getValue().charm.name;

      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client);

      Address addressF = new Address();
      addressF.cia_id = clientDetailsEntry.getKey();
      addressF.client_num = ind;
      addressF.type = clientDetailsEntry.getValue().addressF.type.toString();
      addressF.street = clientDetailsEntry.getValue().addressF.street;
      addressF.house = clientDetailsEntry.getValue().addressF.house;
      addressF.flat = clientDetailsEntry.getValue().addressF.flat;

      Address addressR = new Address();
      addressR.cia_id = clientDetailsEntry.getKey();
      addressR.client_num = ind;
      addressR.type = clientDetailsEntry.getValue().addressR.type.toString();
      addressR.street = clientDetailsEntry.getValue().addressR.street;
      addressR.house = clientDetailsEntry.getValue().addressR.house;
      addressR.flat = clientDetailsEntry.getValue().addressR.flat;

      migrationTestDao.get().insertAddress(ciaMigrationWorker.tmpAddrTable, addressF);
      migrationTestDao.get().insertAddress(ciaMigrationWorker.tmpAddrTable, addressR);
      expectedAddresses.add(addressF);
      expectedAddresses.add(addressR);

      ind++;
    }

    ciaMigrationWorker.checkForClientExistence();
    ciaMigrationWorker.upsertClients();

    //
    //
    ciaMigrationWorker.upsertClientAddress();
    //
    //

    List<Address> actualUpsertedAddresses = clientTestDao.get().loadAddressList();

    assertThat(actualUpsertedAddresses).isNotNull();
    assertThat(actualUpsertedAddresses).hasSameSizeAs(expectedAddresses);
    for (int i = 0; i < actualUpsertedAddresses.size(); i++) {
      assertThatAreEqual(actualUpsertedAddresses.get(i), expectedAddresses.get(i));
    }
  }

  @Test
  public void test_exclusionOfDuplicatePhoneNumbers() throws Exception {
    clientTestDao.get().removeAllData();

    List<PhoneNumber> expectedPhoneNumbers = new ArrayList<>();
    Map<String, ClientDetails> uniqueGoodClientDetails = fileGenerator.getUniqueGoodClientDetails();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    int client_ind = 0;
    int phone_ind = 0;
    for (Map.Entry<String, ClientDetails> clientDetailsEntry : uniqueGoodClientDetails.entrySet()) {
      for (kz.greetgo.sandbox.controller.model.PhoneNumber phone : clientDetailsEntry.getValue().phoneNumbers) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.id = phone_ind++;
        phoneNumber.client_num = client_ind;
        phoneNumber.type = PhoneType.valueOf(phone.phoneType.toString());
        phoneNumber.phone_number = phone.number;
        migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumber);
        phoneNumber.id = phone_ind++;
        migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumber);
        expectedPhoneNumbers.add(phoneNumber);
      }
      client_ind++;
    }

    //
    //
    ciaMigrationWorker.markDuplicatePhoneNumbers();
    //
    //

    List<PhoneNumber> actualUniquePhoneNumbers = migrationTestDao.get().loadUniquePhoneNumbers(ciaMigrationWorker.tmpPhoneTable);

    assertThat(actualUniquePhoneNumbers).isNotNull();
    assertThat(actualUniquePhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    for (int i = 0; i < actualUniquePhoneNumbers.size(); i++) {
      assertThatAreEqual(actualUniquePhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void test_upsertPhoneNumbers() throws Exception {
    clientTestDao.get().removeAllData();

    List<PhoneNumber> expectedPhoneNumbers = new ArrayList<>();
    Map<String, ClientDetails> uniqueGoodClientDetails = fileGenerator.getUniqueGoodClientDetails();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    int client_ind = 0;
    int phone_ind = 0;
    for (Map.Entry<String, ClientDetails> clientDetailsEntry : uniqueGoodClientDetails.entrySet()) {
      ClientTmp client = new ClientTmp();
      client.id = client_ind;
      client.cia_id = clientDetailsEntry.getKey();
      client.name = clientDetailsEntry.getValue().name;
      client.surname = clientDetailsEntry.getValue().surname;
      client.patronymic = clientDetailsEntry.getValue().patronymic;
      client.gender = clientDetailsEntry.getValue().gender.toString();
      client.birth_date = new SimpleDateFormat("yyyy-MM-dd").parse(clientDetailsEntry.getValue().dateOfBirth);
      client.charm_name = clientDetailsEntry.getValue().charm.name;

      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client);

      for (kz.greetgo.sandbox.controller.model.PhoneNumber phone : clientDetailsEntry.getValue().phoneNumbers) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.id = phone_ind++;
        phoneNumber.client_num = client_ind;
        phoneNumber.type = PhoneType.valueOf(phone.phoneType.toString());
        phoneNumber.phone_number = phone.number;
        migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumber);
        expectedPhoneNumbers.add(phoneNumber);
      }

      client_ind++;
    }

    ciaMigrationWorker.checkForClientExistence();
    ciaMigrationWorker.upsertClients();

    //
    //
    ciaMigrationWorker.upsertPhoneNumbers();
    //
    //

    List<PhoneNumber> actualUpsertedPhoneNumbers = clientTestDao.get().loadPhoneNumberList();

    assertThat(actualUpsertedPhoneNumbers).isNotNull();
    assertThat(actualUpsertedPhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    for (int i = 0; i < actualUpsertedPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualUpsertedPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void test_wholeMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    Set<String> expectedClientIds = fileGenerator.getGoodClientIds();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();
    ciaMigrationWorker.inputStream = new FileInputStream(fileGenerator.getOutCiaFileName());

    //
    //
    ciaMigrationWorker.migrate();
    //
    //

    Set<String> actualClientCiaIds = clientTestDao.get().getClientCiaIdsSet();

    assertThat(actualClientCiaIds).isEqualTo(expectedClientIds);
  }

  private void assertThatAreEqual(PhoneNumber actualPhoneNumber, PhoneNumber expectedPhoneNumber) {
    assertThat(actualPhoneNumber.type).isEqualTo(expectedPhoneNumber.type);
    assertThat(actualPhoneNumber.phone_number).isEqualTo(expectedPhoneNumber.phone_number);
  }

  private void assertThatAreEqual(Address actualAddress, Address expectedAddress) {
    assertThat(actualAddress.type).isEqualTo(expectedAddress.type);
    assertThat(actualAddress.street).isEqualTo(expectedAddress.street);
    assertThat(actualAddress.house).isEqualTo(expectedAddress.house);
    assertThat(actualAddress.flat).isEqualTo(expectedAddress.flat);
  }

  private void assertThatAreEqual(ClientTmp actualClient, ClientTmp expectedClient) {
    assertThat(actualClient.cia_id).isEqualTo(expectedClient.cia_id);
    assertThat(actualClient.name).isEqualTo(expectedClient.name);
    assertThat(actualClient.surname).isEqualTo(expectedClient.surname);
    assertThat(actualClient.patronymic).isEqualTo(expectedClient.patronymic);
    assertThat(actualClient.gender).isEqualTo(expectedClient.gender);
    assertThat(actualClient.charm_name).isEqualTo(expectedClient.charm_name);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(actualClient.birth_date != null ? sdf.format(actualClient.birth_date) : null)
      .isEqualTo(expectedClient.birth_date != null ? sdf.format(expectedClient.birth_date) : null);
  }

  private GenerateInputFiles prepareInputFiles(int ciaLimit, int frsLimit) throws Exception {
    GenerateInputFiles fileGenerator = new GenerateInputFiles(ciaLimit, frsLimit);
    fileGenerator.setTestMode();
    fileGenerator.execute();
    return fileGenerator;
  }

  private CiaMigrationWorker getCiaMigrationWorker() {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection);
    ciaMigrationWorker.outError = outError;
    ciaMigrationWorker.reportXlsx = reportXlsx;
    ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return ciaMigrationWorker;
  }

}