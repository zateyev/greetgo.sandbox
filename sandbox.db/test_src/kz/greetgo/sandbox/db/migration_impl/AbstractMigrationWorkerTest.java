package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link AbstractMigrationWorker}
 */
public class AbstractMigrationWorkerTest extends ParentTestNg {

  public BeanGetter<CiaMigrationWorker> migration;
  public BeanGetter<FrsMigrationWorker> frsMigration;
  public BeanGetter<TmpClientTestDao> tmpClientTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  @Test
  public void testFrsMigration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    int recordsSize = frsMigration.get().migrate();
    //
    //

    assertThat(recordsSize).isEqualTo(0);
  }
}