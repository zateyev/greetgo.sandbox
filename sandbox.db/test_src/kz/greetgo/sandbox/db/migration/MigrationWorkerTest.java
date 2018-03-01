package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link MigrationWorker}
 */
public class MigrationWorkerTest extends ParentTestNg {

  public BeanGetter<MigrationWorker> migration;
  public BeanGetter<TmpClientTestDao> tmpClientTestDao;

  @Test
  public void testMigrate() throws Exception {
//    tmpClientTestDao.get().cleanDb();

    //
    //
    int recordsSize = migration.get().migrate();
    //
    //

    assertThat(recordsSize).isEqualTo(0);
  }

}