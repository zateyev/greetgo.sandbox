package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

public interface CharmTestDao {
  @Select("TRUNCATE Charm CASCADE")
  void removeAllData();

  @Insert("insert into Charm (id, name, description, energy) values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") String id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") double energy);

  @Select("SELECT name FROM charm")
  Set<String> loadCharmNamesSet();
}
