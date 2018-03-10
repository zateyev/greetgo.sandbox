package kz.greetgo.sandbox.server.schedulers;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration_impl.Migration;
import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;

@Bean
public class MigrationScheduler {
  public BeanGetter<Migration> migration;

  @FromConfig("Миграция Cia")
  @Scheduled("08-18:00/10   {mon-fri}")
  public void migrateCia() throws Exception {
    migration.get().executeCiaMigration();
  }

  @FromConfig("Миграция Frs")
  @Scheduled("*:00   {mon-fri}")
  public void migrateFrs() throws Exception {
    migration.get().executeFrsMigration();
  }
}
