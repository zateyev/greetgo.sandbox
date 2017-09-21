package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.stand_db.beans.StandDb;
import org.apache.log4j.Logger;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;

  public BeanGetter<AuthTestDao> authTestDao;

  public void loadTestData() {
    logger.info("Start loading test data...");

    logger.info("Loading persons...");
    standDb.get().personStorage.values().forEach(authTestDao.get()::insertPersonDot);

    logger.info("Finish loading test data");
  }
}
