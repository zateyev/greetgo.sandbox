package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ClientTestDao {
  //  @Select("TRUNCATE Charm; TRUNCATE Client; TRUNCATE ClientAddr; TRUNCATE ClientPhone; TRUNCATE ClientAccount; " +
//  "TRUNCATE TransactionType; TRUNCATE ClientAccountTransaction")
  @Select("TRUNCATE client CASCADE; TRUNCATE client_phone")
  void removeAllData();

  @Select("SELECT client.id, client.surname, client.name, client.patronymic, client.gender, " +
    "client.birth_date, client.charm FROM client WHERE id = #{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "surname", column = "surname"),
    @Result(property = "name", column = "name"),
    @Result(property = "patronymic", column = "patronymic"),
    @Result(property = "gender", column = "gender"),
    @Result(property = "dateOfBirth", column = "birth_date"),
    @Result(property = "charm.id", column = "charm")
  })
  ClientDetails getClientDetailsById(@Param("id") String clientId);

  @Select("select count(1) from Client")
  long countOfClients(@Param("filterBy") String filterBy,
                      @Param("filterInputs") String filterInputs);

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

  @Select("SELECT client, type, street, house, flat " +
    "FROM client_addr WHERE client = #{client} and type = #{type}")
  @Results({
    @Result(property = "id", column = "client"),
    @Result(property = "type", column = "type"),
    @Result(property = "street", column = "street"),
    @Result(property = "house", column = "house"),
    @Result(property = "flat", column = "flat")
  })
  Address getAddrByClientId(@Param("client") String clientId,
                               @Param("type") AddressType type);

  @Select("SELECT number, type " +
    "FROM client_phone WHERE client = #{client}")
  @Results({
    @Result(property = "phoneType", column = "type"),
    @Result(property = "number", column = "number")
  })
  List<PhoneNumber> getPhonesByClientId(@Param("client") String clientId);

  @Select("SELECT count(1) FROM client_account_transaction")
  long getTransactionCount();

  @Select("SELECT count(1) FROM client_account")
  long getAccountCount();

  @Select("SELECT cia_id FROM client")
  Set<String> getClientCiaIdsSet();

  @Select("SELECT surname FROM client WHERE cia_id = #{cia_id}")
  String getClientSurnameByCiaId(@Param("cia_id") String key);
}
