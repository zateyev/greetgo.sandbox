package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import org.apache.ibatis.annotations.Select;

@Bean
public interface MigrationTestDaoPostgres extends MigrationTestDao {
  @Select("TRUNCATE cia_migration_client_20180307; TRUNCATE cia_migration_addr_20180307; TRUNCATE cia_migration_phone_20180307")
  void cleanDb();
}
