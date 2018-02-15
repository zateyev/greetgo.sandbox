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
//  void insertOrUpdateClient(ClientRecords clientRecords);

//  @Select("select count(1) from Client where position(#{filterInputs} in ${filterBy}) <> 0")
//  long getTotalSize(@Param("filterBy") String filterBy,
//                    @Param("filterInputs") String filterInputs);
}
