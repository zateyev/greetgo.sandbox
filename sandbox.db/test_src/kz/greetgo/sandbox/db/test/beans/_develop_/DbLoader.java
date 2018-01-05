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

import java.util.ArrayList;
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
    String charmId2 = RND.str(5);
    String charmId3 = RND.str(5);
    String charmName = "Хороший";
    String charmName2 = "Плохой";
    String charmName3 = "Средний";

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> surnames = new ArrayList<>();
    ArrayList<String> patronymic = new ArrayList<>();
    ArrayList<String> charms = new ArrayList<>();

    names.add("Соломон");   surnames.add("Кульчицкий");   patronymic.add("Афанасиевич");
    names.add("Викентий");  surnames.add("Ягужинский");   patronymic.add("Платонович");
    names.add("Серафим");   surnames.add("Якурин");       patronymic.add("Евстафиевич");
    names.add("Елисей");    surnames.add("Рыжков");       patronymic.add("Ермолаевич");
    names.add("Клавдий");   surnames.add("Гуляев");       patronymic.add("Денисович");
    names.add("Мир");       surnames.add("Руских");       patronymic.add("Брониславович");
    names.add("Эммануил");  surnames.add("Безбородов");   patronymic.add("Наумович");
    names.add("Михаил");    surnames.add("Карташов");     patronymic.add("Федорович");
    names.add("Трофим");    surnames.add("Климов");       patronymic.add("Федосиевич");
    names.add("Данила");    surnames.add("Лассман");      patronymic.add("Адрианович");
    names.add("Анатолий");  surnames.add("Колесников");   patronymic.add("Филимонович");
    names.add("Мирослав");  surnames.add("Арзамасцев");   patronymic.add("Самуилович");
    names.add("Леондий");   surnames.add("Кутичев");      patronymic.add("Андронович");
    names.add("Агафон");    surnames.add("Синицын");      patronymic.add("Тихонович");
    names.add("Даниил");    surnames.add("Умский");       patronymic.add("Измаилович");

    charms.add(charmId);
    charms.add(charmId2);
    charms.add(charmId3);



    clientTestDao.get().insertCharm(charmId, charmName);
    clientTestDao.get().insertCharm(charmId2, charmName2);
    clientTestDao.get().insertCharm(charmId3, charmName3);

    for (int i = 100; i < 150; i++) {
      String clientId = RND.str(10);
      clientTestDao.get().insertClient(
        clientId,
        names.get(RND.plusInt(14)),
        surnames.get(RND.plusInt(14)),
        patronymic.get(RND.plusInt(14)),
        java.sql.Date.valueOf("199"+ RND.intStr(1) +"-10-10"),
        getgender(RND.bool()),
        charms.get(RND.plusInt(2))
      );

      clientTestDao.get().insertAdrr(clientId,
        "reg",
        surnames.get(14),
        RND.intStr(2),
        RND.intStr(2));
      clientTestDao.get().insertAdrr(clientId,
        "fact",
        surnames.get(14),
        RND.intStr(3),
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

  public String getgender(boolean t){
    if(t == true) return "male";
    else return "female";
  }
}
