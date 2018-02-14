package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.dao.AuthDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Bean
public interface ClientDaoPostgres extends ClientDao {
  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{id}, #{surname}, #{name}), #{patronymic}, #{gender}, #{birth_date}, #{charm}) " +
    "on conflict (id) do update set surname = #{surname}, name = #{name}, patronymic = #{patronymic}, " +
    "gender = #{gender}, birth_date = #{birth_date}, charm = #{charm}")
  void saveClient(@Param("id") long personId,
                  @Param("surname") String surname,
                  @Param("name") String name,
                  @Param("patronymic") String patronymic,
                  @Param("gender") Gender gender,
                  @Param("birth_date") LocalDate birth_date,
                  @Param("charm") long charm);
}
