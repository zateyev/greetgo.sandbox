package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.MigrationWorker;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {
    MigrationWorker migrationWorker = new MigrationWorker();
    while (true) {
      int count = migrationWorker.migrate();
      if (count == 0) break;
    }
  }
}
