package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.sandbox.controller.migration.MigrationWorker;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/migration")
public class MigrationController {

  public BeanGetter<MigrationWorker> migrationRegister;

  @Mapping("/migrateCia")
  public int migrateCia() throws Exception {
    return migrationRegister.get().migrateCia();
  }
}