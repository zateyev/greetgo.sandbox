package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.dao.CharmDao;
import org.apache.ibatis.annotations.Select;

@Bean
public interface CharmDaoPostgres extends CharmDao {
  @Select("SELECT id FROM charm WHERE record_state=0 ORDER BY id ASC LIMIT 1")
  int selectFirstRowId();
}
