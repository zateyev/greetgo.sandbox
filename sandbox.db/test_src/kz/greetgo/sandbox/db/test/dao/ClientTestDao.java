package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

public interface ClientTestDao {
  @Select("select count(*) from Client where position(#{filterInputs} in ${filterBy}) <> 0")
  long loadTotalSize(@Param("filterBy") String filterBy,
                    @Param("filterInputs") String filterInputs);

  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{id}, #{surname}, #{name}), #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") String personId,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") LocalDate birth_date,
                    @Param("charm") long charm);
}
