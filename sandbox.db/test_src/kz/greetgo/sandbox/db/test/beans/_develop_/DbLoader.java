package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.TokenRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.function.Function;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;

  public BeanGetter<AuthTestDao> authTestDao;

  public BeanGetter<TokenRegister> tokenManager;

  public BeanGetter<ClientTestDao> clientTestDao;

  public void loadTestData() {
    logger.info("Start loading test data...");

    logger.info("Loading persons...");
    Function<String, String> passwordEncryption = tokenManager.get()::encryptPassword;
    standDb.get().personStorage.values().stream()
      .peek(p -> p.encryptedPassword = passwordEncryption.apply(p.password))
      .peek(PersonDot::showInfo)
      .forEach(authTestDao.get()::insertPersonDot);

    String charmId = RND.str(5);
    String charmName = RND.str(10);
    clientTestDao.get().insertCharm(charmId, charmName);
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));

    for (int i = 100; i < 150; i++) {
      String clientId = RND.str(10);
      clientTestDao.get().insertClient(
        clientId,
        RND.str(10),
        RND.str(10),
        RND.str(10),
        java.sql.Date.valueOf("1990-10-10"),
        "male",
        charmId
      );

      clientTestDao.get().insertAdrr(clientId,
        "reg",
        RND.str(5),
        RND.str(5),
        RND.intStr(3));
      clientTestDao.get().insertAdrr(clientId,
        "fact",
        RND.str(5),
        RND.str(5),
        RND.intStr(3));

      clientTestDao.get().insertPhones(
        clientId,
        "work",
        RND.intStr(8)
      );
      clientTestDao.get().insertPhones(
        clientId,
        "home",
        RND.intStr(8)
      );
      clientTestDao.get().insertPhones(
        clientId,
        "mobile",
        RND.intStr(8)
      );

      clientTestDao.get().insertClientAccount(
        RND.str(10),
        clientId,
        (float) RND.plusDouble(999999, 2),
        RND.str(5),
        new Date()
      );
      clientTestDao.get().insertClientAccount(
        RND.str(10),
        clientId,
        (float) RND.plusDouble(999999, 2),
        RND.str(5),
        new Date()
      );
    }

    logger.info("Finish loading test data");

  }
}
