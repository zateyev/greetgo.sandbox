package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Bean
public interface ClientDaoPostgres extends ClientDao {

}
