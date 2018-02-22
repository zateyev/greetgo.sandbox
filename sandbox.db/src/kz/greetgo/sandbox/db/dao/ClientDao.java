package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface ClientDao {
  @Select("SELECT Client.id, Client.surname, Client.name, Client.patronymic, Client.gender, " +
    "Client.birth_date, Client.charm FROM Client WHERE id = #{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "surname", column = "surname"),
    @Result(property = "name", column = "name"),
    @Result(property = "patronymic", column = "patronymic"),
    @Result(property = "gender", column = "gender"),
    @Result(property = "dateOfBirth", column = "birth_date"),
    @Result(property = "charm.id", column = "charm")
  })
  ClientDetails selectClientDetailsById(@Param("id") String clientId);

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

  @Delete("DELETE FROM ClientAccount WHERE client = #{client}")
  void removeClientAccount(@Param("client") String clientsId);

  @Delete("DELETE FROM ClientPhone WHERE client = #{client}")
  void removePhoneNumbersOfClient(@Param("client") String clientsId);

  @Select("SELECT client, type, street, house, flat " +
    "FROM ClientAddr WHERE client = #{client} and type = #{type}")
  @Results({
    @Result(property = "id", column = "client"),
    @Result(property = "type", column = "type"),
    @Result(property = "street", column = "street"),
    @Result(property = "house", column = "house"),
    @Result(property = "flat", column = "flat")
  })
  Address selectAddrByClientId(@Param("client") String clientId,
                               @Param("type") AddressType type);

  @Select("SELECT number, type " +
    "FROM ClientPhone WHERE client = #{client}")
  @Results({
    @Result(property = "phoneType", column = "type"),
    @Result(property = "number", column = "number")
  })
  List<PhoneNumber> selectPhonesByClientId(@Param("client") String clientId);
//  void insertOrUpdateClient(ClientRecords clientRecords);

//  @Select("select count(1) from Client where position(#{filterInputs} in ${filterBy}) <> 0")
//  long getTotalSize(@Param("filterBy") String filterBy,
//                    @Param("filterInputs") String filterInputs);
}
