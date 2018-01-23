package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.*;

import java.sql.Date;

public interface ClientTestDao {
  //Client table part
  @Select("SELECT COUNT(*) FROM Client WHERE disabled=false")
  long selectEnabledCountTableClient();

  @Delete("DELETE FROM Client")
  void deleteAllTableClient();

  @Update("UPDATE Client SET disabled=true")
  void disableAllTableClient();

  @Update("ALTER SEQUENCE client_id_seq RESTART")
  void restartSequenceIdTableClient();

  @Insert("INSERT INTO Client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (nextval('client_id_seq'), #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm);

  //Charm table part
  @Delete("DELETE FROM Charm")
  void deleteAllTableCharm();

  @Select("SELECT COUNT(*) FROM Charm")
  int selectAllCountTableCharm();

  @Update("ALTER SEQUENCE charm_id_seq RESTART")
  void restartSequenceIdTableCharm();

  @Insert("INSERT INTO Charm (id, name, description, energy) " +
    "VALUES (nextval('charm_id_seq'), #{name}, #{description}, #{energy})")
  void insertCharm(@Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") float energy);
}
