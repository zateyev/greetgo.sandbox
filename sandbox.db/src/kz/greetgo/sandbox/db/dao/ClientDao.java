package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientDao {
  @Select("SELECT COUNT(*) " +
    "FROM Client " +
    "WHERE disabled=false AND " +
      "(surname LIKE '%#{filterName}%' OR " +
      "name LIKE '%#{filterName}%' OR " +
      "patronymic LIKE '%#{filterName}%')")
  long selectEnabledWithFilterCountTableClient(@Param("filterName") String filterName);
}
