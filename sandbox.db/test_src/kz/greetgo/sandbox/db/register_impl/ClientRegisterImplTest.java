package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.util.Util;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
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
    this.declareAndInsertCharms();

    long expectedClientCount = 10;
    this.generateAndInsertClients(expectedClientCount, 0);

    long realClientCount = clientTestDao.get().selectEnabledCountTableClient();

    Assertions.assertThat(expectedClientCount).isEqualTo(realClientCount);
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
    this.declareAndInsertCharms();

    long expectedClientCount = 40;
    this.generateAndInsertClients(expectedClientCount, 0);

    long realClientCount = clientRegister.get().getCount("");

    Assertions.assertThat(expectedClientCount).isEqualTo(realClientCount);
  }

  @Test
  public void method_getCount_filter() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long expectedFilteredClientCount = 0;

    {
      String surname = "Нурбакыт";
      String name = "Айбек";
      String patronymic = "Смагулович";
      String gender = Gender.MALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "Исаков";
      String name = "Владимир";
      String patronymic = "Вячеславович";
      String gender = Gender.MALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
    }

    {
      String surname = "Яковлева";
      String name = "Нургиза";
      String patronymic = "Андреевна";
      String gender = Gender.FEMALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "asd";
      String name = "dsa";
      String patronymic = "sda";
      String gender = Gender.EMPTY.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
    }

    {
      String surname = "Яковлева";
      String name = "Татьяна";
      String patronymic = "Нурлановна";
      String gender = Gender.FEMALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    long realFilteredClientCount = clientRegister.get().getCount("Нур");

    Assertions.assertThat(expectedFilteredClientCount).isEqualTo(realFilteredClientCount);
  }


  @Test
  public void method_getCount_filterWithIgnoredCase() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long expectedFilteredClientCount = 0;

    {
      String surname = "Нурбакыт";
      String name = "Айбек";
      String patronymic = "Смагулович";
      String gender = Gender.MALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "Исаков";
      String name = "Владимир";
      String patronymic = "Вячеславович";
      String gender = Gender.MALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
    }

    {
      String surname = "Яковлева";
      String name = "Нургиза";
      String patronymic = "Андреевна";
      String gender = Gender.FEMALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "asd";
      String name = "dsa";
      String patronymic = "Айнур";
      String gender = Gender.EMPTY.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "Яковлева";
      String name = "Татьяна";
      String patronymic = "Нурлановна";
      String gender = Gender.FEMALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    {
      String surname = "Nur";
      String name = "a";
      String patronymic = "sdwqe";
      String gender = Gender.EMPTY.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
    }

    long realFilteredClientCount = clientRegister.get().getCount("нУР");

    Assertions.assertThat(expectedFilteredClientCount).isEqualTo(realFilteredClientCount);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countToSkipNegative() {
    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = -10;
    clientRecordListRequest.clientRecordCount = 10;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countZero() {
    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = 0;
    clientRecordListRequest.clientRecordCount = 0;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countNegative() {
    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = 0;
    clientRecordListRequest.clientRecordCount = -10;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test
  public void method_getRecordList_default() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long clientCount = 30, expectedClientRecordCount = 20;
    this.generateAndInsertClients(clientCount, 0);

    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = 0;
    clientRecordListRequest.clientRecordCount = expectedClientRecordCount;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    List<ClientRecord> recordList = clientRegister.get().getRecordList(clientRecordListRequest);

    long realClientRecordCount = recordList.size();

    Assertions.assertThat(expectedClientRecordCount).isEqualTo(realClientRecordCount);
  }

  @Test
  public void method_getRecordList_defaultWithPagination1() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long clientCount = 80, expectedClientRecordCount = 33;
    this.generateAndInsertClients(clientCount, 0);

    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = 0;
    clientRecordListRequest.clientRecordCount = expectedClientRecordCount;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    List<ClientRecord> recordList = clientRegister.get().getRecordList(clientRecordListRequest);

    long realClientRecordCount = recordList.size();

    Assertions.assertThat(expectedClientRecordCount).isEqualTo(realClientRecordCount);
  }

  @Test
  public void method_getRecordList_defaultWithPagination2() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long clientCount = 80, clientRecordCount = 33, clientRecordToSkip = 25;
    long expectedClientRecordCount = clientRecordCount;
    this.generateAndInsertClients(clientCount, 0);

    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = clientRecordToSkip;
    clientRecordListRequest.clientRecordCount = clientRecordCount;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    List<ClientRecord> recordList = clientRegister.get().getRecordList(clientRecordListRequest);

    long realClientRecordCount = recordList.size();

    Assertions.assertThat(expectedClientRecordCount).isEqualTo(realClientRecordCount);
  }

  @Test
  public void method_getRecordList_defaultWithPagination3() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    long clientCount = 80, clientRecordCount = 33, clientRecordToSkip = 71;
    long expectedClientRecordCount = clientCount - clientRecordToSkip;
    this.generateAndInsertClients(clientCount, 0);

    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = clientRecordToSkip;
    clientRecordListRequest.clientRecordCount = clientRecordCount;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    List<ClientRecord> recordList = clientRegister.get().getRecordList(clientRecordListRequest);

    long realClientRecordCount = recordList.size();

    Assertions.assertThat(expectedClientRecordCount).isEqualTo(realClientRecordCount);
  }

  @Test
  public void method_getRecordList_sortAge() {
    this.resetTablesAll();
    this.declareAndInsertCharms();

    /*List<ClientRecord>

    {
      String surname = "Яковлева";
      String name = "Татьяна";
      String patronymic = "Нурлановна";
      String gender = Gender.FEMALE.name();
      Date date = Date.valueOf(LocalDate.now());
      int charm = 1;

      clientTestDao.get().insertClient(surname, name, patronymic, gender, date, charm);
      expectedFilteredClientCount++;
    }

    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = clientRecordToSkip;
    clientRecordListRequest.clientRecordCount = clientRecordCount;
    clientRecordListRequest.columnSortType = ColumnSortType.NONE;
    clientRecordListRequest.sortAscend = false;
    clientRecordListRequest.nameFilter = "";

    List<ClientRecord> recordList = clientRegister.get().getRecordList(clientRecordListRequest);

    long realClientRecordCount = recordList.size();

    Assertions.assertThat(expectedClientRecordCount).isEqualTo(realClientRecordCount);*/
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
