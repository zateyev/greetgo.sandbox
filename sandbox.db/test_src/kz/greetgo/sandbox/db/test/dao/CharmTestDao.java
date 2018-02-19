package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CharmTestDao {
  @Select("TRUNCATE Charm")
  void removeAllData();

  @Insert("insert into Charm (id, name, description, energy) values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") String id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") double energy);
}
