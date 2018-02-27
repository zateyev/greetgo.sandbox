package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.Migration;

public class LaunchMigration {

  public static void main(String[] args) {
    Migration migration = new Migration();
    while (true) {
      migration.migrate();
    }
  }
}
