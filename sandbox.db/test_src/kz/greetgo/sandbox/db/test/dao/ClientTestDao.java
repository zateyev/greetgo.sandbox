package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.PhoneNumber;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.migration_impl.model.*;
import org.apache.ibatis.annotations.*;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ClientTestDao {
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

  @Select("SELECT c.id, c.surname, c.name, c.patronymic, c.gender, c.birth_date, ch.name as charm" +
    " FROM client c LEFT JOIN charm ch ON c.charm = ch.id WHERE cia_id = #{cia_id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "surname", column = "surname"),
    @Result(property = "name", column = "name"),
    @Result(property = "patronymic", column = "patronymic"),
    @Result(property = "gender", column = "gender"),
    @Result(property = "dateOfBirth", column = "birth_date"),
    @Result(property = "charm.name", column = "charm")
  })
  ClientDetails getClientDetailsByCiaId(@Param("cia_id") String ciaId);

  @Select("select count(1) from Client")
  long getClientCount();

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

  @Select("SELECT client, type, street, house, flat " +
    "FROM client_addr WHERE client = #{client} and type = #{type}")
  @Results({
    @Result(property = "id", column = "client"),
    @Result(property = "type", column = "type"),
    @Result(property = "street", column = "street"),
    @Result(property = "house", column = "house"),
    @Result(property = "flat", column = "flat")
  })
  Address selectAddrByClientId(@Param("client") String clientId,
                               @Param("type") AddressType type);

  @Select("SELECT number FROM client_account WHERE" +
    " client = (SELECT id FROM client WHERE cia_id = #{cia_id}) AND registered_at = #{registered_at}")
  String getClientAccountByCiaId(@Param("cia_id") String ciaId, @Param("registered_at") Date registeredAtD);

  @Select("SELECT account as account_number, money, type as transaction_type " +
    "FROM client_account_transaction WHERE account = " +
    "(SELECT id FROM client_account WHERE number = #{account_number}) AND finished_at = #{finished_at}")
  TransactionTmp getTransactionByAccountNumber(@Param("account_number") String accountNumber,
                                               @Param("finished_at") Date finishedAt);

  void insertClientTmp(ClientTmp client);

  @Select("SELECT cia_id, surname, client.name, patronymic, gender, birth_date, charm.name AS charm_name FROM client LEFT JOIN charm" +
    " ON client.charm = charm.id ORDER BY surname")
  List<ClientTmp> loadClientList();

  @Select("SELECT type, street, house, flat FROM client_addr ORDER BY client")
  List<kz.greetgo.sandbox.db.migration_impl.model.Address> loadAddressList();

  @Select("SELECT type, number as phone_number FROM client_phone ORDER BY client")
  List<kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber> loadPhoneNumberList();

  @Select("SELECT number as account_number, registered_at as registeredAtD FROM client_account ORDER BY registered_at")
  List<AccountTmp> loadAccountList();

  @Select("SELECT money, type as transaction_type FROM client_account_transaction ORDER BY finished_at")
  List<TransactionTmp> loadTransactionList();

  void insertAccountTmp(AccountTmp account);
}
