package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationManager;

@Bean
public class MigrationRegisterImpl implements MigrationRegister{

  public BeanGetter<MigrationManager> migrationManager;

  @Override
  public void doMigrate() throws Exception {
    migrationManager.get().migrate();
  }
}
