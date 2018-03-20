package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

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

//  private File outErrorFile;
  private OutputStream outError;
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

//    outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
//    outErrorFile.getParentFile().mkdirs();
    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();

    File outErrorFile = new File("build/out_files/error_report.txt");
    //noinspection ResultOfMethodCallIgnored
    outErrorFile.getParentFile().mkdirs();
    outError = new FileOutputStream(outErrorFile);
  }

  @AfterMethod
  public void closeResources() throws Exception {
//    migrationTestDao.get().dropTmpTables(tmpClientTableName, tmpAddrTableName, tmpPhoneTableName);

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

    ciaMigrationWorker.inputStream = new FileInputStream(fileGenerator.getOutCiaFileName());

    ciaMigrationWorker.createTmpTables();

    //
    //
    ciaMigrationWorker.parseDataAndSaveInTmpDb();
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
    assertThat(actualAddresses).hasSameSizeAs(expectedAddresses);
    for (int i = 0; i < actualAddresses.size(); i++) {
      assertThatAreEqual(actualAddresses.get(i), expectedAddresses.get(i));
    }

    assertThat(actualPhoneNumbers).isNotNull();
    assertThat(actualPhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    // sorting because generator shuffles tags
    actualPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number));
    expectedPhoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number));
    for (int i = 0; i < actualPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void testValidation() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);

    List<Client> generatedClients = fileGenerator.getGeneratedClients();
    List<Client> expectedErrorClients = fileGenerator.getErrorClients();

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

    List<Client> actualErrorClients = migrationTestDao.get().loadErrorClientsList(ciaMigrationWorker.tmpClientTable);

    assertThat(actualErrorClients).isNotNull();
    assertThat(actualErrorClients).hasSameSizeAs(expectedErrorClients);
    for (int i = 0; i < actualErrorClients.size(); i++) {
      assertThatAreEqual(actualErrorClients.get(i), expectedErrorClients.get(i));
    }
  }

  @Test
  public void test_markingOfDuplicateClientRecords() throws Exception {
//    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();
//
//    GenerateInputFiles fileGenerator = prepareInputFiles(100, 0);
//
//    List<Client> generatedClients = fileGenerator.getGeneratedClients();
//    List<Client> expectedErrorClients = fileGenerator.getErrorClients();
//
//    ciaMigrationWorker.createTmpTables();
//
//    for (int i = 0; i < generatedClients.size(); i++) {
//      generatedClients.get(i).id = i;
//      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, generatedClients.get(i));
//    }
//
//    //
//    //
//    ciaMigrationWorker.markDuplicateClientRecords();
//    //
//    //
//
//    List<Client> actualErrorClients = migrationTestDao.get().loadErrorClientsList(ciaMigrationWorker.tmpClientTable);
//
//    assertThat(actualErrorClients).isNotNull();
//    assertThat(actualErrorClients).hasSameSizeAs(expectedErrorClients);
//    for (int i = 0; i < actualErrorClients.size(); i++) {
//      assertThatAreEqual(actualErrorClients.get(i), expectedErrorClients.get(i));
//    }
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

  private void markTmpTablesToDrop(CiaMigrationWorker ciaMigrationWorker) {
    tmpClientTableName = ciaMigrationWorker.tmpClientTable;
    tmpAddrTableName = ciaMigrationWorker.tmpAddrTable;
    tmpPhoneTableName = ciaMigrationWorker.tmpPhoneTable;
  }

  private CiaMigrationWorker getCiaMigrationWorker() {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection);
    ciaMigrationWorker.outError = outError;
    ciaMigrationWorker.reportXlsx = reportXlsx;
    ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return ciaMigrationWorker;
  }

}