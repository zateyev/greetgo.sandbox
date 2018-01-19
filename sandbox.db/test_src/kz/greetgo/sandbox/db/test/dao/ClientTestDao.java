package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface ClientTestDao {
  @Delete("DELETE FROM Client")
  void clearTableClient();

  @Insert("INSERT INTO Client (id, surnamename, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date")LocalDate date,
                    @Param("charm")int charm);


}
