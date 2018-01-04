package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.migration.Migration;
import kz.greetgo.sandbox.db.migration.SAXPars;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Year;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;


  @Test
  public void getClient_CREATE() throws Exception {

    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));

    //
    //
    ClientDetails details = clientRegister.get().getClient(null);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.id).isNull();
    assertThat(details.charmId).isNull();
    assertThat(details.charms).isNotNull();
    assertThat(details.charms).hasSize(2);
  }

  @Test
  public void getClient_UPDATE() throws Exception {

    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();

    String charmId1 = RND.str(10), charmName1 = "B" + RND.str(10);
    String charmId2 = RND.str(10), charmName2 = "A" + RND.str(10);

    clientTestDao.get().insertCharm(charmId1, charmName1);
    clientTestDao.get().insertCharm(charmId2, charmName2);

    String clientId = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);
    String gender = "male";
    java.sql.Date birthDate = java.sql.Date.valueOf("1991-11-11");

    clientTestDao.get().insert(clientId);

    clientTestDao.get().insertAdrr(clientId,
      "reg",
      "StreetForReg",
      "HouseForReg",
      "FlatForReg");

    clientTestDao.get().insertAdrr(clientId,
      "fact",
      "StreetForFact",
      "HouseForFact",
      "FlatForFact");

    clientTestDao.get().insertPhones(
      clientId,
      "work",
      "1111145"
    );

    clientTestDao.get().insertPhones(
      clientId,
      "home",
      "2222245"
    );

    clientTestDao.get().insertPhones(
      clientId,
      "mobile",
      "3333345"
    );

    clientTestDao.get().insertPhones(
      clientId,
      "mobile",
      "4444445"
    );

    clientTestDao.get().update(clientId, "charm_id", charmId1);
    clientTestDao.get().update(clientId, "surname", surname);
    clientTestDao.get().update(clientId, "name", name);
    clientTestDao.get().update(clientId, "birth_date", birthDate);
    clientTestDao.get().update(clientId, "current_gender", gender);
    clientTestDao.get().update(clientId, "actual", 1);

    //
    //
    ClientDetails details = clientRegister.get().getClient(clientId);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.id).isEqualTo(clientId);
    assertThat(details.surname).isEqualTo(surname);
    assertThat(details.name).isEqualTo(name);
    assertThat(details.dateOfBirth).isEqualTo(birthDate.toString());
    assertThat(details.gender).isEqualTo(gender);
    assertThat(details.charmId).isEqualTo(charmId1);
    assertThat(details.charms).isNotNull();


    assertThat(details.charms).hasSize(2);
    assertThat(details.charms.get(0).name).isEqualTo(charmName2);
    assertThat(details.charms.get(1).name).isEqualTo(charmName1);

    assertThat(details.factAddress.street).isEqualTo("StreetForFact");
    assertThat(details.factAddress.house).isEqualTo("HouseForFact");
    assertThat(details.factAddress.flat).isEqualTo("FlatForFact");

    assertThat(details.regAddress.street).isEqualTo("StreetForReg");
    assertThat(details.regAddress.house).isEqualTo("HouseForReg");
    assertThat(details.regAddress.flat).isEqualTo("FlatForReg");

    assertThat(details.phones.work).isEqualTo("1111145");
    assertThat(details.phones.home).isEqualTo("2222245");
    assertThat(details.phones.mobile.get(0)).isEqualTo("3333345");
    assertThat(details.phones.mobile.get(1)).isEqualTo("4444445");

  }


  @Test
  public void deleteClient_NOTNULLid() throws Exception {
    deleteAll();

    String clientId = RND.str(5);
    String clientId2 = RND.str(5);
    String charmId = RND.str(5);

    insertCharm(charmId);
    insertClient(clientId, charmId);
    insertClient(clientId2, charmId);
    insertAddresses(clientId);
    insertAddresses(clientId2);
    insertPhones(clientId);
    insertPhones(clientId2);
    insertAccount(clientId);
    insertAccount(clientId2);

    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    clientTestDao.get().update(clientId2, "name", RND.str(10));
    clientTestDao.get().update(clientId2, "surname", RND.str(10));
    clientTestDao.get().update(clientId2, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId2, "actual", 1);

    //
    //
    clientRegister.get().deleteClient(clientId);
    //
    //

    assertThat(clientTestDao.get().getActualClient(clientId)).isEqualTo(0);
    assertThat(clientTestDao.get().getFactAddress(clientId)).isNull();
    assertThat(clientTestDao.get().getRegAddress(clientId)).isNull();
    assertThat(clientTestDao.get().getHomePhone(clientId)).isNullOrEmpty();
    assertThat(clientTestDao.get().getWorkPhone(clientId)).isNullOrEmpty();
    assertThat(clientTestDao.get().getMobile(clientId)).isNullOrEmpty();
    assertThat(clientTestDao.get().getClientAccount(clientId)).isNullOrEmpty();

    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo(1);
    assertThat(clientTestDao.get().getFactAddress(clientId2)).isNotNull();
    assertThat(clientTestDao.get().getRegAddress(clientId2)).isNotNull();
    assertThat(clientTestDao.get().getHomePhone(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getWorkPhone(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getMobile(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getClientAccount(clientId2)).isNotEmpty();

  }


  @Test
  public void deleteClient_NULLid() {

    deleteAll();

    String clientId = RND.str(5);
    String clientId2 = RND.str(5);
    String charmId = RND.str(5);

    insertCharm(charmId);
    insertClient(clientId, charmId);
    insertClient(clientId2, charmId);
    insertAddresses(clientId);
    insertAddresses(clientId2);
    insertPhones(clientId);
    insertPhones(clientId2);
    insertAccount(clientId);
    insertAccount(clientId2);

    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    clientTestDao.get().update(clientId2, "name", RND.str(10));
    clientTestDao.get().update(clientId2, "surname", RND.str(10));
    clientTestDao.get().update(clientId2, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId2, "actual", 1);

    //
    //
    clientRegister.get().deleteClient(null);
    //
    //


    assertThat(clientTestDao.get().getActualClient(clientId)).isEqualTo(1);
    assertThat(clientTestDao.get().getFactAddress(clientId)).isNotNull();
    assertThat(clientTestDao.get().getRegAddress(clientId)).isNotNull();
    assertThat(clientTestDao.get().getHomePhone(clientId)).isNotEmpty();
    assertThat(clientTestDao.get().getWorkPhone(clientId)).isNotEmpty();
    assertThat(clientTestDao.get().getMobile(clientId)).isNotEmpty();
    assertThat(clientTestDao.get().getClientAccount(clientId)).isNotEmpty();

    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo(1);
    assertThat(clientTestDao.get().getFactAddress(clientId2)).isNotNull();
    assertThat(clientTestDao.get().getRegAddress(clientId2)).isNotNull();
    assertThat(clientTestDao.get().getHomePhone(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getWorkPhone(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getMobile(clientId2)).isNotEmpty();
    assertThat(clientTestDao.get().getClientAccount(clientId2)).isNotEmpty();

  }

  @Test
  public void saveClient_NULLid() {

    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllCharms();

    ClientToSave cl = new ClientToSave();
    ClientAddress fact = new ClientAddress();
    ClientAddress reg = new ClientAddress();
    ClientPhones phones = new ClientPhones();

    String charmId = RND.str(10);
    String charmName = RND.str(10);

    fact.street = "street";
    fact.house = "house";
    fact.flat = "flat";
    reg.street = "street";
    reg.house = "house";
    reg.flat = "flat";


    phones.home = "7878787878787";
    phones.work = "7878787878788";
    phones.mobile.add("7878787878887");
    phones.mobile.add("7888787878787");


    clientTestDao.get().insertCharm(charmId, charmName);

    cl.id = null;
    cl.name = RND.str(10);
    cl.surname = RND.str(10);
    cl.patronymic = RND.str(10);
    cl.charmId = charmId;
    cl.dateOfBirth = "1992-11-12";
    cl.gender = "male";
    cl.factAddress = fact;
    cl.regAddress = reg;
    cl.phones = phones;

    //
    //
    ClientRecord rec = clientRegister.get().saveClient(cl);
    //
    //

    ClientDetails actual = clientTestDao.get().loadDetails(rec.id);
    ClientAddress addr = clientTestDao.get().getFactAddress(rec.id);
    String homePhone = clientTestDao.get().getHomePhone(rec.id);
    String workPhone = clientTestDao.get().getWorkPhone(rec.id);
    List<String> mobile = clientTestDao.get().getMobile(rec.id);


    assertThat(actual).isNotNull();
    assertThat(actual.name).isEqualTo(cl.name);
    assertThat(actual.surname).isEqualTo(cl.surname);
    assertThat(actual.patronymic).isEqualTo(cl.patronymic);
    assertThat(actual.charmId).isEqualTo(cl.charmId);
    assertThat(actual.dateOfBirth).isEqualTo(cl.dateOfBirth);
    assertThat(actual.gender).isEqualTo(cl.gender);

    assertThat(addr.street).isEqualTo("street");
    assertThat(addr.house).isEqualTo("house");
    assertThat(addr.flat).isEqualTo("flat");

    assertThat(homePhone).isEqualTo(phones.home);
    assertThat(workPhone).isEqualTo(phones.work);
    assertThat(mobile).isEqualTo(phones.mobile);

  }

  @Test
  public void saveClient_NOTNULLid() {

    String clientId = RND.str(10);
    String name = RND.str(10);
    String surname = RND.str(10);
    String patronymic = RND.str(10);

    String charmId = RND.str(5);
    String charmName = RND.str(10);

    clientTestDao.get().insertCharm(charmId, charmName);
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllPhones();
    clientTestDao.get().deleteAllAddr();

    clientTestDao.get().insertClient(
      clientId,
      name,
      surname,
      patronymic,
      java.sql.Date.valueOf("1990-10-10"),
      "male",
      charmId);

    insertAddresses(clientId);

    insertPhones(clientId);

    insertAccountWithMoney(clientId, 15961);

    ClientToSave clUpdated = new ClientToSave();
    ClientAddress fact = new ClientAddress();
    ClientAddress reg = new ClientAddress();
    ClientPhones phone = new ClientPhones();

    fact.street = "street";
    fact.house = "house";
    fact.flat = "flat";
    reg.street = "";
    reg.house = "";
    reg.flat = "";

    phone.home = "123123";
    phone.work = "456456";
    phone.mobile.add("678678");

    clUpdated.id = clientId;
    clUpdated.name = name + "new";
    clUpdated.surname = surname + "new";
    clUpdated.patronymic = "";
    clUpdated.dateOfBirth = "1990-11-11";
    clUpdated.gender = "female";
    clUpdated.charmId = charmId;
    clUpdated.factAddress = fact;
    clUpdated.regAddress = reg;
    clUpdated.phones = phone;

    //
    //
    ClientRecord rec = clientRegister.get().saveClient(clUpdated);
    //
    //

    ClientDetails actual = clientTestDao.get().loadDetails(rec.id);
    ClientAddress factAddr = clientTestDao.get().getFactAddress(rec.id);
    ClientAddress regAddr = clientTestDao.get().getRegAddress(rec.id);
    String homePhone = clientTestDao.get().getHomePhone(rec.id);
    String workPhone = clientTestDao.get().getWorkPhone(rec.id);
    List<String> mobile = clientTestDao.get().getMobile(rec.id);

    assertThat(actual.name).isEqualTo(clUpdated.name);
    assertThat(actual.surname).isEqualTo(clUpdated.surname);
    assertThat(actual.patronymic).isEqualTo(clUpdated.patronymic);
    assertThat(actual.dateOfBirth).isEqualTo(clUpdated.dateOfBirth);

    assertThat(factAddr.street).isEqualTo("street");
    assertThat(factAddr.house).isEqualTo("house");
    assertThat(factAddr.flat).isEqualTo("flat");

    assertThat(regAddr.street).isNullOrEmpty();
    assertThat(regAddr.house).isNullOrEmpty();
    assertThat(regAddr.flat).isNullOrEmpty();

    assertThat(homePhone).isEqualTo(phone.home);
    assertThat(workPhone).isEqualTo(phone.work);
    assertThat(mobile).isEqualTo(phone.mobile);

  }

  @Test
  public void getList_emptyList() {
    clientTestDao.get().deleteAllClients();

    ClientListRequest req = new ClientListRequest();

    //
    //
    List<ClientRecord> rec = clientRegister.get().getList(req);
    //
    //

    assertThat(rec).hasSize(0);
  }

  @Test
  public void getList_CheckReturnedRecord() {

    deleteAll();
    String id = RND.str(10);
    String charmId = RND.str(5);
    String charmName = RND.str(10);
    Float money = (float) RND.plusDouble(99999, 5);

    clientTestDao.get().insertCharm(charmId, charmName);
    clientTestDao.get().insertClient(
      id,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      java.sql.Date.valueOf("1990-10-10"),
      "male",
      charmId
    );

    clientTestDao.get().insertClientAccount(
      RND.str(5),
      id,
      money,
      RND.str(5),
      new Date()
    );

    ClientListRequest req = new ClientListRequest();

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    ClientRecord rc = clientTestDao.get().getClient(id);

    assertThat(list).hasSize(1);
    assertThat(list.get(0).fio).isEqualTo(rc.fio);
    assertThat(list.get(0).age).isEqualTo(rc.age);
    assertThat(list.get(0).charm).isEqualTo(charmName);
    assertThat(list.get(0).totalAccountBalance).isEqualTo(money);
    assertThat(list.get(0).maxAccountBalance).isEqualTo(money);
    assertThat(list.get(0).minAccountBalance).isEqualTo(money);

  }

  @Test
  public void getList_CheckLimitedList() {
    deleteAll();
    String charmId = RND.str(5);
    String clientName = RND.str(10);

    insertCharm(charmId);

    for (int i = 0; i < 25; i++) {
      String clientId = RND.str(10);

      clientTestDao.get().insertClient(
        clientId,
        clientName,
        RND.str(10),
        RND.str(10),
        java.sql.Date.valueOf("1990-10-10"),
        "male",
        charmId
      );

      clientTestDao.get().insertClientAccount(
        RND.str(10),
        clientId,
        (float) RND.plusDouble(999999, 2),
        RND.str(5),
        new Date()
      );
    }


    ClientListRequest req = new ClientListRequest();
    req.count = 5;
    req.skipFirst = 10;

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    List<String> detList = clientTestDao.get().getListOfIds();

    assertThat(list).hasSize(5);
    for (int i = 0; i < 5; i++) assertThat(detList.get(10 + i)).isEqualTo(list.get(0 + i).id);


  }

  @Test
  public void getList_CheckFilteredList_byName() {
    String charmId = RND.str(5);

    deleteAll();

    insertCharm(charmId);

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClientWithName(clientId, charmId, "Иван" + RND.str(5));
      insertAccount(clientId);
    }

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClient(clientId, charmId);
      insertAccount(clientId);
    }

    ClientListRequest req = new ClientListRequest();
    req.filterByFio = "Иван";

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(10);
    for (ClientRecord rec : list) assertThat(rec.fio).contains("Иван");

  }

  @Test
  public void getList_CheckFilteredList_bySurname() {
    String charmId = RND.str(5);

    deleteAll();

    insertCharm(charmId);

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClientWithSurname(clientId, charmId, "Иван" + RND.str(5));
      insertAccount(clientId);
    }

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClient(clientId, charmId);
      insertAccount(clientId);
    }

    ClientListRequest req = new ClientListRequest();
    req.filterByFio = "Иван";

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(10);
    for (ClientRecord rec : list) assertThat(rec.fio).contains("Иван");

  }

  @Test
  public void getList_CheckFilteredList_byPatronymic() {
    String charmId = RND.str(5);

    deleteAll();

    insertCharm(charmId);

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClientWithPatronymic(clientId, charmId, "Иван" + RND.str(5));
      insertAccount(clientId);
    }

    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);
      insertClient(clientId, charmId);
      insertAccount(clientId);
    }

    ClientListRequest req = new ClientListRequest();
    req.filterByFio = "Иван";

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(10);
    for (ClientRecord rec : list) assertThat(rec.fio).contains("Иван");

  }

  @Test
  public void getList_CheckSortedList_totalAccountBalanceAsc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "total";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).totalAccountBalance).isEqualTo(5+1+2+3);
    assertThat(list.get(1).totalAccountBalance).isEqualTo(62.41f);
    assertThat(list.get(2).totalAccountBalance).isEqualTo(754);

  }

  @Test
  public void getList_CheckSortedList_totalAccountBalanceDesc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "totalDesc";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).totalAccountBalance).isEqualTo(754);
    assertThat(list.get(1).totalAccountBalance).isEqualTo(62);
    assertThat(list.get(2).totalAccountBalance).isEqualTo(11);

  }

  @Test
  public void getList_CheckSortedList_maxAccountBalanceAsc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "max";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).maxAccountBalance).isEqualTo(5);
    assertThat(list.get(1).maxAccountBalance).isEqualTo(25);
    assertThat(list.get(2).maxAccountBalance).isEqualTo(522);

  }

  @Test
  public void getList_CheckSortedList_maxAccountBalanceDesc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "maxDesc";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).maxAccountBalance).isEqualTo(522);
    assertThat(list.get(1).maxAccountBalance).isEqualTo(25);
    assertThat(list.get(2).maxAccountBalance).isEqualTo(5);

  }

  @Test
  public void getList_CheckSortedList_minAccountBalanceAsc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "min";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).minAccountBalance).isEqualTo(1);
    assertThat(list.get(1).minAccountBalance).isEqualTo(15);
    assertThat(list.get(2).minAccountBalance).isEqualTo(232);

  }

  @Test
  public void getList_CheckSortedList_minAccountBalanceDesc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "minDesc";
    req.count = 5;
    req.skipFirst = 0;

    String clientId1 = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);
    String charmId = RND.str(10);

    insertCharm(charmId);

    insertClientWithCharm(clientId1, charmId);
    insertClientWithCharm(clientId2, charmId);
    insertClientWithCharm(clientId3, charmId);

    insertAccountWithMoney(clientId1, 25.47f);
    insertAccountWithMoney(clientId1, 15.47f);
    insertAccountWithMoney(clientId1, 21.47f);

    insertAccountWithMoney(clientId2, 232);
    insertAccountWithMoney(clientId2, 522);

    insertAccountWithMoney(clientId3, 5);
    insertAccountWithMoney(clientId3, 1);
    insertAccountWithMoney(clientId3, 2);
    insertAccountWithMoney(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).minAccountBalance).isEqualTo(232);
    assertThat(list.get(1).minAccountBalance).isEqualTo(15);
    assertThat(list.get(2).minAccountBalance).isEqualTo(1);

  }

  @Test
  public void getList_CheckSortedList_ClientAgeDesc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "ageDesc";
    req.count = 5;
    req.skipFirst = 0;

    String charmId = RND.str(10);

    String clientId = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);

    insertCharm(charmId);

    insertClientWithDate(clientId, charmId, "2010-07-07");
    insertClientWithDate(clientId2, charmId, "2015-07-07");
    insertClientWithDate(clientId3, charmId, "2000-07-07");

    insertAccountWithMoney(clientId, 456456f);
    insertAccountWithMoney(clientId, 456f);
    insertAccountWithMoney(clientId2,654.45f);
    insertAccountWithMoney(clientId3,654.45f);

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    int year = Year.now().getValue();

    assertThat(list.get(0).age).isEqualTo(year-2000);
    assertThat(list.get(1).age).isEqualTo(year-2010);
    assertThat(list.get(2).age).isEqualTo(year-2015);

  }

  @Test
  public void getList_CheckSortedList_ClientAgeAsc() {

    deleteAll();

    ClientListRequest req = new ClientListRequest();
    req.sort = "age";
    req.count = 5;
    req.skipFirst = 0;

    String charmId = RND.str(10);

    String clientId = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);

    insertCharm(charmId);

    insertClientWithDate(clientId, charmId, "2010-07-07");
    insertClientWithDate(clientId2, charmId, "2015-07-07");
    insertClientWithDate(clientId3, charmId, "2000-07-07");

    insertAccountWithMoney(clientId, 456456f);
    insertAccountWithMoney(clientId, 456f);
    insertAccountWithMoney(clientId2,654.45f);
    insertAccountWithMoney(clientId3,654.45f);

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);

    int year = Year.now().getValue();

    assertThat(list.get(0).age).isEqualTo(year -2015);
    assertThat(list.get(1).age).isEqualTo(year -2010);
    assertThat(list.get(2).age).isEqualTo(year -2000);

  }
  @Test
  public void getSize_ofFilteredList() {

    ClientListRequest req = new ClientListRequest();
    deleteAll();

    req.filterByFio = "abba";

    String charmId = RND.str(10);
    insertCharm(charmId);

    for (int i = 0; i < 5; i++) {
      String clientId = RND.str(10);
      insertClient(clientId, charmId);
    }

    String clientId = RND.str(10);
    String clientId2 = RND.str(10);
    String clientId3 = RND.str(10);

    insertClientWithName(clientId, charmId, "abba" + RND.str(5));
    insertClientWithSurname(clientId2, charmId, "abba" + RND.str(5));
    insertClientWithPatronymic(clientId3, charmId, "abba" + RND.str(5));

    //
    //
    long size = clientRegister.get().getSize(req);
    //
    //

    ClientRecord det = clientTestDao.get().getClient(clientId);
    ClientRecord det2 = clientTestDao.get().getClient(clientId2);
    ClientRecord det3 = clientTestDao.get().getClient(clientId3);

    assertThat(size).isEqualTo(3);
    assertThat(det.fio).contains("abba");
    assertThat(det2.fio).contains("abba");
    assertThat(det3.fio).contains("abba");

  }

  @Test
  public void getSize_ofList() {

    ClientListRequest req = new ClientListRequest();
    deleteAll();

    String charmId = RND.str(10);
    insertCharm(charmId);

    for (int i = 0; i < 50; i++) {
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

      insertAccountWithMoney(clientId, 265.841f);
    }

    //
    //
    long size = clientRegister.get().getSize(req);
    //
    //

    assertThat(size).isEqualTo(50);

  }




  @Test
  public void download_Xlsx() throws Exception {

    ClientListRequest req = new ClientListRequest();
    req.count = 0;

    OutputStream stream = new FileOutputStream("hello.xlsx");

    //
    //
    clientRegister.get().download(req, stream, "xlsx", "p1");
    //
    //

  }

  @Test
  public void testMigration() throws ParserConfigurationException, SAXException, IOException {
    Migration m = new Migration();
    m.parseRecordData();

    ClientToSave cl = m.getClient();

    //
    //
    clientTestDao.get().insertClient(
      cl.id,
      cl.name,
      cl.surname,
      cl.patronymic,
      java.sql.Date.valueOf(cl.dateOfBirth),
      cl.gender,
      cl.charmId);
  }

  private void deleteAll() {
    clientTestDao.get().deleteAllAddr();
    clientTestDao.get().deleteAllPhones();
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
  }

  private void insertCharm(String charmId) {
    clientTestDao.get().insertCharm(charmId, RND.str(10));
  }

  private void insertClient(String clientId, String charmId) {
    clientTestDao.get().insertClient(
      clientId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      java.sql.Date.valueOf("1991-12-12"),
      "male",
      charmId
    );
  }

  private void insertClientWithName(String clientId, String charmId, String name) {
    clientTestDao.get().insertClient(
      clientId,
      name,
      RND.str(10),
      RND.str(10),
      java.sql.Date.valueOf("1991-12-12"),
      "male",
      charmId
    );
  }

  private void insertClientWithSurname(String clientId, String charmId, String surname) {
    clientTestDao.get().insertClient(
      clientId,
      RND.str(10),
      surname,
      RND.str(10),
      java.sql.Date.valueOf("1991-12-12"),
      "male",
      charmId
    );
  }

  private void insertClientWithPatronymic(String clientId, String charmId, String patronymic) {
    clientTestDao.get().insertClient(
      clientId,
      RND.str(10),
      RND.str(10),
      patronymic,
      java.sql.Date.valueOf("1991-12-12"),
      "male",
      charmId
    );
  }


  private void insertClientWithCharm(String clientId, String charmId) {
    clientTestDao.get().insertClient(
      clientId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      java.sql.Date.valueOf("1990-10-10"),
      "male",
      charmId
    );
  }

  private void insertClientWithDate(String clientId, String charmId, String date) {
    clientTestDao.get().insertClient(
      clientId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      java.sql.Date.valueOf(date),
      "male",
      charmId
    );
  }

  private void insertAccount(String clientId){
    clientTestDao.get().insertClientAccount(
      RND.str(10),
      clientId,
      (float) RND.plusDouble(99999, 5),
      RND.str(5),
      new Date()
    );
  }

  private void insertAccountWithMoney(String clientId, float money) {
    clientTestDao.get().insertClientAccount(
      RND.str(10),
      clientId,
      money,
      RND.str(5),
      new Date()
    );
  }


  private void insertAddresses(String clientId) {
    clientTestDao.get().insertAdrr(
      clientId,
      "fact",
      RND.str(10),
      RND.str(10),
      RND.str(10)
    );

    clientTestDao.get().insertAdrr(
      clientId,
      "reg",
      RND.str(10),
      RND.str(10),
      RND.str(10)
    );
  }

  private void insertPhones(String clientId){
    clientTestDao.get().insertPhones(
      clientId,
      "home",
      RND.intStr(10)
    );
    clientTestDao.get().insertPhones(
      clientId,
      "work",
      RND.intStr(10)
    );
    clientTestDao.get().insertPhones(
      clientId,
      "mobile",
      RND.intStr(10)
    );
  }


}