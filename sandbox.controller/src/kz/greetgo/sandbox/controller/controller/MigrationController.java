package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;


@Bean
@Mapping("/migration")
public class MigrationController implements Controller {

  public BeanGetter<MigrationRegister> migrationRegister;

  @NoSecurity
  @Mapping("/doMigrate")
  public void doMigrate() throws Exception {
    migrationRegister.get().doMigrate();
  }

}
