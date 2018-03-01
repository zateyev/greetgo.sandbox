package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.test.dao.TmpClientTestDao;
import org.apache.ibatis.annotations.Select;

@Bean
public interface TmpClientTestDaoPostgres extends TmpClientTestDao {
  @Select("TRUNCATE tmp_client; TRUNCATE tmp_charm; TRUNCATE tmp_addr; TRUNCATE tmp_phone")
  void cleanDb();
}
