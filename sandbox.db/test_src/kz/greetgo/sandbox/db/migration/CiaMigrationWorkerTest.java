package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration_impl.CiaMigrationWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link CiaMigrationWorker}
 */
public class CiaMigrationWorkerTest extends ParentTestNg {

  public BeanGetter<CiaMigrationWorker> migration;
  public BeanGetter<TmpClientTestDao> tmpClientTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  @Test
  public void testMigrate() throws Exception {
//    tmpClientTestDao.get().cleanDb();
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    //
    //
    int recordsSize = migration.get().migrate();
    //
    //

    assertThat(recordsSize).isEqualTo(0);
  }

}