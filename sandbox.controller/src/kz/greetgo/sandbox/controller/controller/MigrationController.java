package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.sandbox.controller.migration.CiaMigrationWorker;
import kz.greetgo.sandbox.controller.migration.FrsMigrationWorker;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/migration")
public class MigrationController {

  public BeanGetter<CiaMigrationWorker> ciaMigrationRegister;
  public BeanGetter<FrsMigrationWorker> frsMigrationRegister;

  @Mapping("/migrateCia")
  public int migrateCia() throws Exception {
    return ciaMigrationRegister.get().migrate();
  }

  @Mapping("/migrateFrs")
  public int migrateFrs() throws Exception {
    return frsMigrationRegister.get().migrate();
  }
}