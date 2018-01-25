package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.annotations.Select;

@Bean
public interface ClientDaoPostgres extends ClientDao {
}
