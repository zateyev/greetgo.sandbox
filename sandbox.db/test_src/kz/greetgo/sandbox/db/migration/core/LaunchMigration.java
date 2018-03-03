package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration_impl.CiaMigrationWorker;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker();
    while (true) {
      int count = ciaMigrationWorker.migrate();
      if (count == 0) break;
    }
  }
}
