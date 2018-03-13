package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link AbstractMigrationWorker}
 */
public class AbstractMigrationWorkerTest extends ParentTestNg {

  public BeanGetter<CiaMigrationWorkerImpl> migration;
  public BeanGetter<FrsMigrationWorkerImpl> frsMigration;
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  private GenerateInputFiles fileGenerator;

  @BeforeClass
  @BeforeMethod
  public void prepareInputFiles() throws Exception {
    fileGenerator = new GenerateInputFiles(500, 500);
    fileGenerator.setTestMode();
    fileGenerator.execute();
  }

  @Test
  public void testCiaMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    migration.get().migrate();
    //
    //

    long clientCount = clientTestDao.get().getClientCount();

    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
  }

  @Test
  public void testAllGoodClientsInserted() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    Set<String> goodClientIds = fileGenerator.getGoodClientIds();

    //
    //
    migration.get().migrate();
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
    migration.get().migrate();
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
    migration.get().migrate();
    //
    //

    assertThat(getLineCountOfFile(migrationConfig.get().sshHomePath() + migrationConfig.get().outErrorFile()))
      .isEqualTo(fileGenerator.getErrorRecordCount());
  }

  @Test
  public void testFrsMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    frsMigration.get().migrate();
    //
    //

    long transactionCount = clientTestDao.get().getTransactionCount();
    long accountCount = clientTestDao.get().getAccountCount();

    assertThat(transactionCount).isEqualTo(fileGenerator.getTransactionCount());
    assertThat(accountCount).isEqualTo(fileGenerator.getAccountCount());
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