package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.Gender;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;
import java.util.Date;

public interface ClientTestDao {
  //  @Select("TRUNCATE Charm; TRUNCATE Client; TRUNCATE ClientAddr; TRUNCATE ClientPhone; TRUNCATE ClientAccount; " +
//  "TRUNCATE TransactionType; TRUNCATE ClientAccountTransaction")
  @Select("TRUNCATE Client CASCADE")
  void removeAllData();

  @Select("select id, surname, name, patronymic from Client where id = #{id}")
  ClientDetails getClientById(@Param("id") String clientId);

  @Select("select count(1) from Client")
  long countOfClients(@Param("filterBy") String filterBy,
                      @Param("filterInputs") String filterInputs);

//  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm) " +
//    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, 'charm')")
//  void insertClient(@Param("id") String clientId,
//                    @Param("surname") String surname,
//                    @Param("name") String name,
//                    @Param("patronymic") String patronymic,
//                    @Param("gender") Gender gender,
//                    @Param("birth_date") Date birth_date,
//                    @Param("charm") String charm);

  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") String personId,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") Date birth_date,
                    @Param("charm") String charmId);

  @Insert("insert into Charm (id, name, description, energy) " +
    "values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") String id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") double energy);

  @Insert("insert into ClientAccount (id, client, money, number, registered_at) " +
    "values (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(@Param("id") String id,
                           @Param("client") String clientId,
                           @Param("money") double money,
                           @Param("number") String number,
                           @Param("registered_at") OffsetDateTime registeredAt);
}
