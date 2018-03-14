package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.migration.MigrationWorker;

@Bean
public class Migration {
  public void executeCiaMigration() throws Exception {
//    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorkerImpl();
    MigrationWorker ciaMigrationWorker = new CiaMigrationWorkerImpl();
    ciaMigrationWorker.migrate();
  }

  public void executeFrsMigration() throws Exception {
//    FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorkerImpl();
    MigrationWorker frsMigrationWorker = new FrsMigrationWorkerImpl();
    frsMigrationWorker.migrate();
  }
}
