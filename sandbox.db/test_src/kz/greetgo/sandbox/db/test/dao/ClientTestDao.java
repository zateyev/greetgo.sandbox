package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ClientTestDao {

  @Select("select actual from client where id = #{id}")
  String getActualClient(@Param("id") String id);

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
