package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmTestDao {
  @Select("TRUNCATE Charm CASCADE")
  void removeAllData();

  @Insert("insert into Charm (id, name) values (#{id}, #{name})")
  void insertCharm(@Param("id") String id, @Param("name") String name);

  @Select("SELECT name FROM charm")
  List<String> loadCharmNamesSet();
}
