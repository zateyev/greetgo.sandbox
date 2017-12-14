package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientDao {


  @Select("select * from Charm where actual = 1 order by name")
  List<CharmRecord> loadCharmList();

  @Select("select c.charm_id charmId, c.* from Client c where id = #{id}")
  ClientDetails loadDetails(@Param("id") String id);

  @Update("update client set actual = 0 where id = #{id}")
  void deleteClient(@Param("id") String id);

  @Update("update client set ${fieldName} = #{value} where id = #{clientId}")
  void saveClient(@Param("fieldName") String fieldName,
                  @Param("value") String value,
                  @Param("clientId") String id);


  @Insert("insert into client (id, name, surname, patronymic, charm_id, actual) values (#{id}, #{name}, #{surname}, " +
    "#{patronymic}, #{charm_id}, 1)")
  void insertClient(@Param("id") String id,
                    @Param("name") String name,
                    @Param("surname") String surname,
                    @Param("patronymic") String patronymic,
                    @Param("charm_id") String charmId);
}
