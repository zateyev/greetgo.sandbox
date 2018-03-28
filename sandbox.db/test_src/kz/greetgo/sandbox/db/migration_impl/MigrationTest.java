package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.AccountTmp;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link Migration}
 */
public class MigrationTest extends ParentTestNg {
  public BeanGetter<Migration> migration;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  @BeforeClass
  public void beforeClass() {
    migration.get().setSshMode(false);
  }

  @Test
  public void testExecuteCiaMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = new GenerateInputFiles(50, 0);
    fileGenerator.setTestMode();
    fileGenerator.execute();

    Set<String> goodClientIds = fileGenerator.getGoodClientIds();

    //
    //
    migration.get().executeCiaMigration();
    //
    //

    Set<String> clientCiaIds = clientTestDao.get().getClientCiaIdsSet();

    assertThat(clientCiaIds).isEqualTo(goodClientIds);
  }

  @Test
  public void testExecuteFrsMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = new GenerateInputFiles(10, 50);
    fileGenerator.setTestMode();
    fileGenerator.execute();

    Map<String, AccountTmp> clientAccounts = fileGenerator.getClientAccounts();

    //
    //
    migration.get().executeFrsMigration();
    //
    //

    for (Map.Entry<String, AccountTmp> accountEntry : clientAccounts.entrySet()) {
      String clientAccountNumber = clientTestDao.get().getClientAccountByCiaId(
        accountEntry.getKey(),
        accountEntry.getValue().registeredAtD
      );

      assertThat(clientAccountNumber).isEqualTo(accountEntry.getValue().account_number);
    }
  }

//  @Test
//  public void test_concurrent_migration_of_cia_and_frs() throws Exception {
//    clientTestDao.get().removeAllData();
//    charmTestDao.get().removeAllData();
//
//    GenerateInputFiles fileGenerator = new GenerateInputFiles(10, 50);
//    fileGenerator.setTestMode();
//    fileGenerator.execute();
//
//    //
//    //
//    migration.get().executeCiaFrsMigrationConcurrently();
//    //
//    //
//
//    long clientCount = clientTestDao.get().getClientCount();
//
//    assertThat(clientCount).isEqualTo(fileGenerator.getGoodClientCount());
//  }
}