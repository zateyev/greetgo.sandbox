package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;

public interface ClientDao {
  @Select("SELECT Client.id, Client.surname, Client.name, Client.patronymic, Client.gender, " +
    "Client.birth_date, Client.charm FROM Client WHERE id = #{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "surname", column = "surname"),
    @Result(property = "name", column = "name"),
    @Result(property = "patronymic", column = "patronymic"),
    @Result(property = "gender", column = "gender"),
    @Result(property = "birth_date", column = "dateOfBirth"),
    @Result(property = "charm.id", column = "charm")
  })
  ClientDetails selectClientDetailsById(@Param("id") String clientId);

//  void insertOrUpdateClient(ClientRecords clientRecords);

  @Select("SELECT Client.id, Client.surname, Client.name, Client.patronymic, " +
    "Client.charm FROM Client WHERE id = #{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "surname", column = "surname"),
    @Result(property = "name", column = "name"),
    @Result(property = "patronymic", column = "patronymic"),
    @Result(property = "charm.id", column = "charm")
  })
  ClientInfo selectClientInfoById(@Param("id") String clientId);

  void insertOrUpdateClient(String id, String surname, String name, String patronymic, Gender gender, Date dateOfBirth, String charm);

  void insertPhoneNumber(String clientId, String number, PhoneType type);

  void insertAddress(String clientId, AddressType type, String street, String house, String flat);

  @Delete("DELETE FROM Client WHERE id = #{id}")
  void removeClientById(@Param("id") String clientsId);

  @Delete("DELETE FROM ClientAddr WHERE client = #{client}")
  void removeAddressOfClient(@Param("client") String clientsId);

  @Delete("DELETE FROM ClientPhone WHERE client = #{client}")
  void removePhoneNumbersOfClient(@Param("client") String clientsId);
//  void insertOrUpdateClient(ClientRecords clientRecords);

//  @Select("select count(1) from Client where position(#{filterInputs} in ${filterBy}) <> 0")
//  long getTotalSize(@Param("filterBy") String filterBy,
//                    @Param("filterInputs") String filterInputs);
}
