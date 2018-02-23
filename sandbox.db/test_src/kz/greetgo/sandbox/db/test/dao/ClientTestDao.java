package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;
import java.util.Date;

public interface ClientTestDao {
  //  @Select("TRUNCATE Charm; TRUNCATE Client; TRUNCATE ClientAddr; TRUNCATE ClientPhone; TRUNCATE ClientAccount; " +
//  "TRUNCATE TransactionType; TRUNCATE ClientAccountTransaction")
  @Select("TRUNCATE client CASCADE; TRUNCATE client_phone")
  void removeAllData();

  @Select("select id, surname, name, patronymic from client where id = #{id}")
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

  @Insert("insert into client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") String personId,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") Date birth_date,
                    @Param("charm") String charmId);

  @Insert("insert into charm (id, name, description, energy) " +
    "values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") String id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") double energy);

  @Insert("insert into client_account (id, client, money, number, registered_at) " +
    "values (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(@Param("id") String id,
                           @Param("client") String clientId,
                           @Param("money") double money,
                           @Param("number") String number,
                           @Param("registered_at") OffsetDateTime registeredAt);

  @Insert("insert into client_addr (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(@Param("client") String clientId,
                     @Param("type") AddressType type,
                     @Param("street") String street,
                     @Param("house") String house,
                     @Param("flat") String flat);

  @Insert("INSERT INTO client_phone (client, number, type) VALUES (#{client}, #{number}, #{type}) " + "" +
    "on conflict do nothing")
  void insertPhoneNumber(@Param("client") String clientId,
                         @Param("number") String number,
                         @Param("type") PhoneType type);
}
