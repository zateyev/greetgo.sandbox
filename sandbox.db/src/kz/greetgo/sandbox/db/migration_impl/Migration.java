package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.migration.CiaMigrationWorker;
import kz.greetgo.sandbox.controller.migration.FrsMigrationWorker;

@Bean
public class Migration {
  public void executeCiaMigration() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorkerImpl();
    ciaMigrationWorker.migrate();
  }

  public void executeFrsMigration() throws Exception {
    FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorkerImpl();
    frsMigrationWorker.migrate();
  }
}
