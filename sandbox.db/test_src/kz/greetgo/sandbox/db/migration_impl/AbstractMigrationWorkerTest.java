package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

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

  @Test
  public void testCiaMigration() throws Exception {
//    tmpClientTestDao.get().cleanDb();
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    int recordsSize = migration.get().migrate();
    //
    //

    assertThat(recordsSize).isNotEqualTo(0);
  }

  @Test
  public void testFrsMigration() throws Exception {
//    clientTestDao.get().removeAllData();
//    charmTestDao.get().removeAllData();

    //
    //
    int recordsSize = frsMigration.get().migrate();
    //
    //

    assertThat(recordsSize).isNotEqualTo(0);
  }
}