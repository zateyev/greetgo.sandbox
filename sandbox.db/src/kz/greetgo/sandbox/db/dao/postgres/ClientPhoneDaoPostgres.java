package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.dao.ClientPhoneDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface ClientPhoneDaoPostgres extends ClientPhoneDao {
  @Insert("INSERT INTO client_phone (client, number, type) " +
    "VALUES(#{client}, #{number}, #{type}) " +
    "ON CONFLICT (client, number) DO UPDATE SET type=#{type}")
  void insert(@Param("client") long client,
              @Param("number") String number,
              @Param("type") String type);
}
