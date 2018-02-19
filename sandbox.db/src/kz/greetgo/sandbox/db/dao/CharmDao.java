package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {
  @Select("SELECT name FROM Charm ORDER BY lower(name)")
  List<String> loadCharmNames();
}
