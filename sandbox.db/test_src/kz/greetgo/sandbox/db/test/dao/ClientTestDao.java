package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ClientTestDao {

  @Select("select actual from client where id = #{id}")
  int getActualClient(@Param("id") String id);

  @Select("select c.current_gender gender," +
    " c.charm_id charmId, c.birth_date dateOfBirth, c.* from Client c where id = #{id}")
  ClientDetails loadDetails(@Param("id") String id);

  @Insert("insert into client (id, name, surname, patronymic, birth_date, current_gender, charm_id, actual) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{birth_date}, #{gender}::gender, #{charm_id}, 1)")
  void insertClient(@Param("id") String id,
                    @Param("name") String name,
                    @Param("surname") String surname,
                    @Param("patronymic") String patronymic,
                    @Param("birth_date") java.sql.Date birthDate,
                    @Param("gender") String gender,
                    @Param("charm_id") String charmId);


  @Update("update charm set actual = 0")
  void deleteAllCharms();

  @Insert("insert into charm (id, name, actual) values (#{id}, #{name}, 1)")
  void insertCharm(@Param("id") String id, @Param("name") String name);

  @Insert("insert into client (id) values (#{clientId})")
  void insert(@Param("clientId") String clientId);

  @Update("update client set ${fieldName} = #{value} where id = #{clientId}")
  void update(@Param("clientId") String clientId,
              @Param("fieldName") String fieldName,
              @Param("value") Object value);
}
