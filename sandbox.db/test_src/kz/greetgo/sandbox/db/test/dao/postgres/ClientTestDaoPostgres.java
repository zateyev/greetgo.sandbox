package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration_impl.model.AccountTmp;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface ClientTestDaoPostgres extends ClientTestDao {

  @Insert("INSERT INTO client (id, cia_id, surname, name, patronymic, gender, birth_date) VALUES" +
    " (#{client.number}, #{client.cia_id}, #{client.surname}, #{client.name}, #{client.patronymic}," +
    " #{client.gender}, #{client.birth_date})")
  void insertClientTmp(@Param("client") ClientTmp client);

  @Insert("INSERT INTO client_account (id, client, number) VALUES" +
    " (#{account.number}, #{account.clientId}, #{account.account_number})")
  void insertAccountTmp(@Param("account") AccountTmp account);
}
