package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration_impl.model.*;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Bean
public interface MigrationTestDaoPostgres extends MigrationTestDao {
  @Select("TRUNCATE cia_migration_client_20180307; TRUNCATE cia_migration_addr_20180307; TRUNCATE cia_migration_phone_20180307")
  void cleanDb();

  @Insert("INSERT INTO ${tableName} (client_id, number, cia_id, surname, name, patronymic, gender, birth_date, charm_name) VALUES" +
    " (#{client.number}, #{client.number}, #{client.cia_id}, #{client.surname}, #{client.name}, #{client.patronymic}," +
    " #{client.gender}, #{client.birth_date}, #{client.charm_name})")
  void insertClient(@Param("tableName") String tableName,
                    @Param("client") ClientTmp client);

  @Insert("INSERT INTO ${tableName} (cia_id, client_num, type, street, house, flat) VALUES" +
    " (#{address.cia_id}, #{address.client_num}, #{address.type}, #{address.street}," +
    " #{address.house}, #{address.flat})")
  void insertAddress(@Param("tableName") String tableName,
                     @Param("address") Address address);

  @Insert("INSERT INTO ${tableName} (number, client_num, phone_number, type) VALUES" +
    " (#{phoneNumber.id}, #{phoneNumber.client_num}, #{phoneNumber.phone_number}, #{phoneNumber.type})")
  void insertPhoneNumber(@Param("tableName") String tableName,
                     @Param("phoneNumber") PhoneNumber phoneNumber);

  @Insert("INSERT INTO ${tableName} (type, client_id, account_number, registered_at) VALUES" +
    " (#{account.type}, #{account.clientId}, #{account.account_number}, #{account.registeredAtD})")
  void insertClientAccount(@Param("tableName") String tableName,
                           @Param("account") Account account);

  @Insert("INSERT INTO ${tableName} (type, money, finished_at, transaction_type, account_number) VALUES (#{transaction.type}," +
    " #{transaction.money}, #{transaction.finishedAtD}, #{transaction.transaction_type}, #{transaction.account_number})")
  void insertAccountTransaction(@Param("tableName") String tableName,
                                @Param("transaction") Transaction transaction);
}
