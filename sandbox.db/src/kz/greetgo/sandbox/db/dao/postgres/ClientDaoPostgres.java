package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParamsTo;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecords;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.Date;

@Bean
public interface ClientDaoPostgres extends ClientDao {
  @Insert("INSERT INTO Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}) " +
    "ON CONFLICT (id) DO UPDATE SET surname = #{surname}, name = #{name}, patronymic = #{patronymic}, " +
    "gender = #{gender}, birth_date = #{birth_date}, charm = #{charm}")
  void insertOrUpdateClient(@Param("id") String clientId,
                            @Param("surname") String surname,
                            @Param("name") String name,
                            @Param("patronymic") String patronymic,
                            @Param("gender") Gender gender,
                            @Param("birth_date") Date birth_date,
                            @Param("charm") String charmId);
}
