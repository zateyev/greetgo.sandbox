package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link Migration}
 */
public class MigrationTest extends ParentTestNg {

  public BeanGetter<Migration> migration;
  public BeanGetter<TmpClientTestDao> tmpClientTestDao;

  @Test
  public void testMigrate() throws Exception {
    tmpClientTestDao.get().cleanDb();

    //
    //
    int recordsSize = migration.get().migrate();
    //
    //

    assertThat(recordsSize).isEqualTo(0);
  }

}