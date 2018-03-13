package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
  public BeanGetter<TmpClientTestDao> tmpClientTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  private GenerateInputFiles fileGenerator;

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

    long clientCount = clientTestDao.get().countOfClients(null, null);

    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
  }

  @Test
  public void testAllGoodClientsInserted() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = new GenerateInputFiles(500, 500);
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

    Map<String, String> lastGoodClientSurnamesFromDuplicates = fileGenerator.getLastGoodClientSurnames();


    //
    //
    migration.get().migrate();
    //
    //

    for (Map.Entry<String, String> lastClientEntry : lastGoodClientSurnamesFromDuplicates.entrySet()) {
      assertThat(Objects.equals(lastClientEntry.getValue(), clientTestDao.get().getClientSurnameByCiaId(lastClientEntry.getKey()))).isTrue();
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

    assertThat(47/*line count of file with errors*/).isEqualTo(fileGenerator.getErrorRecordCount());
  }

  @Test
  public void testFrsMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    int recordsSize = frsMigration.get().migrate();
    //
    //

    long transactionCount = clientTestDao.get().getTransactionCount();
    long accountCount = clientTestDao.get().getAccountCount();

    assertThat(transactionCount).isEqualTo(fileGenerator.getTransactionCount());
    assertThat(accountCount).isEqualTo(fileGenerator.getAccountCount());
  }
}