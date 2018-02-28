package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.Migration;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {
    Migration migration = new Migration();
    while (true) {
      int count = migration.migrate();
      if (count == 0) break;
    }
  }
}
