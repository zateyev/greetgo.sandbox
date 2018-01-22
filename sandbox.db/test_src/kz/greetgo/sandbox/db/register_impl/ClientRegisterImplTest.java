package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.util.Util;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.sql.Date;
import java.util.Random;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void insertTableCharm_ok() {
    this.resetTablesAll();

    int expectedCharmCount = this.declareAndInsertCharms();
    int realCharmCount = clientTestDao.get().selectAllCountTableCharm();

    Assertions.assertThat(expectedCharmCount).isEqualTo(realCharmCount);
  }

  @Test
  public void insertTableClient_ok() {
    this.resetTablesAll();

    int charmCount = this.declareAndInsertCharms();
    long expectedClientCount = 10;
    this.generateAndInsertClients(expectedClientCount, charmCount);

    long realClientCount = clientTestDao.get().selectEnabledCountTableClient();

    Assertions.assertThat(realClientCount).isEqualTo(expectedClientCount);
  }

  @Test(expectedExceptions = org.apache.ibatis.exceptions.PersistenceException.class)
  public void pkCharmIdFkClientCharm_exist() {
    this.resetTablesAll();

    int charmCount = this.declareAndInsertCharms();

    String surname = "";
    String name = "";
    String patronymic = "";
    String gender = Gender.EMPTY.name();
    Date date = Date.valueOf("2018-01-02");
    int charm = charmCount + 10;

    clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getCount_null() {
    clientRegister.get().getCount(null);
  }

  @Test
  public void method_getCount_filterEmpty() {
    this.resetTablesAll();

    int charmCount = this.declareAndInsertCharms();
    long expectedClientCount = 40;
    this.generateAndInsertClients(expectedClientCount, charmCount);

    long realClientCount = clientRegister.get().getCount("");

    Assertions.assertThat(realClientCount).isEqualTo(expectedClientCount);
  }

  private int declareAndInsertCharms() {
    int charmCount = 0;

    clientTestDao.get().insertCharm("Не указан", "Неизвестно", 0f);
    charmCount++;

    clientTestDao.get().insertCharm("Спокойный", "Само спокойствие", 10f);
    charmCount++;

    clientTestDao.get().insertCharm("Буйный", "Лучше лишний раз не трогать", 30f);
    charmCount++;

    clientTestDao.get().insertCharm("Загадочный", "О чем он думает?", 8f);
    charmCount++;

    clientTestDao.get().insertCharm("Открытый", "С ним приятно общаться!", 20f);
    charmCount++;

    clientTestDao.get().insertCharm("Понимающий", "Он всегда выслушает", 15f);
    charmCount++;

    clientTestDao.get().insertCharm("Консервативный", "Скучно...", 5f);
    charmCount++;

    return charmCount;
  }

  private void generateAndInsertClient(int charmCount) {
    Random random = new Random();
    String surname = Util.generateString(random.nextInt(5) + 5, false);
    String name = Util.generateString(random.nextInt(5) + 5, false);
    String patronymic = Util.generateString(random.nextInt(10) + 5, false);
    String gender = Gender.values()[random.nextInt(Gender.values().length)].name();
    Date date = Date.valueOf(Util.generateDateString());
    int charm = new Random().nextInt(charmCount) + 1;

    clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
  }

  private void generateAndInsertClients(long clientCount, int charmCount) {
    for (long i = 0; i < clientCount; i++) {
      this.generateAndInsertClient(charmCount);
    }
  }

  private void resetTablesAll() {
    clientTestDao.get().deleteAllTableClient();
    clientTestDao.get().restartSequenceIdTableClient();
    clientTestDao.get().deleteAllTableCharm();
    clientTestDao.get().restartSequenceIdTableCharm();
  }
}
