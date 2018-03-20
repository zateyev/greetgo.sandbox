package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Bean
public interface MigrationTestDaoPostgres extends MigrationTestDao {
  @Select("TRUNCATE cia_migration_client_20180307; TRUNCATE cia_migration_addr_20180307; TRUNCATE cia_migration_phone_20180307")
  void cleanDb();

  @Insert("INSERT INTO ${tableName} (number, cia_id, surname, name, patronymic, gender, birth_date, charm_name) VALUES" +
    " (#{client.id}, #{client.cia_id}, #{client.surname}, #{client.name}, #{client.patronymic}," +
    " #{client.gender}, #{client.birth_date}, #{client.charm_name})")
  void insertClient(@Param("tableName") String tableName,
                    @Param("client") Client client);
}
