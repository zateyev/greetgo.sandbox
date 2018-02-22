package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.dao.CharmDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface CharmDaoPostgres extends CharmDao {
  @Insert("insert into charm (id, name, description, energy) " +
    "values (#{id}, #{name}), #{description}, #{energy}) " +
    "on conflict (id) do update set name = #{name}, description = #{description}, " +
    "energy = #{energy}")
  void saveClient(@Param("id") long id,
                  @Param("name") String name,
                  @Param("description") String description,
                  @Param("energy") double energy);
}
