package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {


  @Select("select * from Charm where actual = 1 order by name")
  List<CharmRecord> loadCharmList();

  @Select("select c.charm_id charmId, c.* from Client c where id = #{id}")
  ClientDetails loadDetails(@Param("id") String id);
}
