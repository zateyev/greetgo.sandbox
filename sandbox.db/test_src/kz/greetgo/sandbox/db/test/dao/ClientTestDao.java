package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.Gender;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface ClientTestDao {
  //  @Select("TRUNCATE Charm; TRUNCATE Client; TRUNCATE ClientAddr; TRUNCATE ClientPhone; TRUNCATE ClientAccount; " +
//  "TRUNCATE TransactionType; TRUNCATE ClientAccountTransaction")
  @Select("TRUNCATE Client")
  void removeAllData();

  @Select("select id, surname, name, patronymic from Client where id = #{id}")
  ClientDetails getClientById(@Param("id") String clientId);

  @Select("select count(1) from Client")
  long countOfClients(@Param("filterBy") String filterBy,
                      @Param("filterInputs") String filterInputs);

  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, 'charm')")
//  void insertClient(ClientDot clientDot);
  void insertClient(@Param("id") String personId,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") Date birth_date,
                    @Param("charm") String charm);
}
