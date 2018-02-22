package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {
  @Select("SELECT id, name, description, energy FROM Charm ORDER BY lower(name)")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "name", column = "name"),
    @Result(property = "description", column = "description"),
    @Result(property = "energy", column = "energy")})
  List<Charm> loadCharmNames();
}
