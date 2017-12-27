package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
      "reg",
      "reg",
      "reg");
    clientTestDao.get().insertAdrr(clientId,
      "fact",
      "fact",
      "fact",
      "fact");

    clientTestDao.get().insertPhones(
      clientId,
      "work",
      "878754878"
    );
    clientTestDao.get().insertPhones(
      clientId,
      "home",
      "878712878"
    );
    clientTestDao.get().insertPhones(
      clientId,
      "mobile",
      "83787878"
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
    assertThat(details.factAddress.street).isEqualTo("fact");
    assertThat(details.factAddress.house).isEqualTo("fact");
    assertThat(details.factAddress.flat).isEqualTo("fact");

    assertThat(details.regAddress.street).isEqualTo("reg");
    assertThat(details.regAddress.house).isEqualTo("reg");
    assertThat(details.regAddress.flat).isEqualTo("reg");

    assertThat(details.phones.home).isEqualTo("878712878");
    assertThat(details.phones.work).isEqualTo("878754878");
    assertThat(details.phones.mobile.get(0)).isEqualTo("83787878");

  }


  @Test
  public void deleteClient_NOTNULLid() throws Exception {

    String clientId = RND.str(5);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    String clientId2 = RND.str(5);

    clientTestDao.get().insert(clientId2);
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
    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo(1);

  }

  @Test
  public void deleteClient_NULLid() {

    String clientId = RND.str(5);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    String clientId2 = RND.str(5);

    clientTestDao.get().insert(clientId2);
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
    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo(1);

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

    ClientToSave clUpdated = new ClientToSave();
    ClientAddress fact = new ClientAddress();
    ClientAddress reg = new ClientAddress();
    ClientPhones phone = new ClientPhones();
    fact.street = "street";
    fact.house = "house";
    fact.flat = "flat";
    reg.street = "street";
    reg.house = "house";
    reg.house = "house";
    reg.flat = "flat";

    phone.home = "123132";
    phone.work = "123133";
    phone.mobile.add("143132");

    clUpdated.id = clientId;
    clUpdated.name = name + "new";
    clUpdated.surname = surname + "new";
    clUpdated.patronymic = patronymic + "new";
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
    ClientAddress addr = clientTestDao.get().getFactAddress(rec.id);
    String homePhone = clientTestDao.get().getHomePhone(rec.id);
    String workPhone = clientTestDao.get().getWorkPhone(rec.id);
    List<String> mobile = clientTestDao.get().getMobile(rec.id);

    assertThat(actual.name).isEqualTo(clUpdated.name);
    assertThat(actual.surname).isEqualTo(clUpdated.surname);
    assertThat(actual.patronymic).isEqualTo(clUpdated.patronymic);
    assertThat(actual.dateOfBirth).isEqualTo(clUpdated.dateOfBirth);
    assertThat(addr.street).isEqualTo("street");
    assertThat(addr.house).isEqualTo("house");
    assertThat(addr.flat).isEqualTo("flat");

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

    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllCharms();
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
    assertThat(list.get(0).totalAccountBalance).isEqualTo(money.longValue());
    assertThat(list.get(0).maxAccountBalance).isEqualTo(money.longValue());
    assertThat(list.get(0).minAccountBalance).isEqualTo(money.longValue());

  }

  @Test
  public void getList_CheckLimitedList() {
    clientTestDao.get().deleteAllAddr();
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
    String charmId = RND.str(5);
    String charmName = RND.str(10);
    String clientName = RND.str(10);

    clientTestDao.get().insertCharm(charmId, charmName);

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
    assertThat(detList.get(10)).isEqualTo(list.get(0).id);

  }

  @Test
  public void getList_CheckFilteredList() {
    String charmId = RND.str(5);
    String charmName = RND.str(10);
    clientTestDao.get().deleteAllAddr();
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
    String clientName = RND.str(10);


    clientTestDao.get().insertCharm(charmId, charmName);
    for (int i = 0; i < 10; i++) {
      String clientId = RND.str(10);

      clientTestDao.get().insertClient(
        clientId,
        "Иванов" + clientName,
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

    for (int i = 0; i < 10; i++) {
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
    req.filterByFio = "Иван";

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(10);


  }

  @Test
  public void getList_CheckSortedList_totalAsc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).totalAccountBalance).isEqualTo(11);
    assertThat(list.get(1).totalAccountBalance).isEqualTo(62);
    assertThat(list.get(2).totalAccountBalance).isEqualTo(754);

  }

  @Test
  public void getList_CheckSortedList_totalDesc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


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
  public void getList_CheckSortedList_maxAsc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


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
  public void getList_CheckSortedList_maxDesc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


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
  public void getList_CheckSortedList_minAsc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


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
  public void getList_CheckSortedList_minDesc() {

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

    insertClient(clientId1, charmId);
    insertClient(clientId2, charmId);
    insertClient(clientId3, charmId);

    insertAccount(clientId1, 25.47f);
    insertAccount(clientId1, 15.47f);
    insertAccount(clientId1, 21.47f);

    insertAccount(clientId2, 232);
    insertAccount(clientId2, 522);

    insertAccount(clientId3, 5);
    insertAccount(clientId3, 1);
    insertAccount(clientId3, 2);
    insertAccount(clientId3, 3);


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
  public void getList_CheckFilteredList_ageDesc(){

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

    insertAccount(clientId, 456456f);
    insertAccount(clientId, 456f);
    insertAccount(clientId2,654.45f);
    insertAccount(clientId3,654.45f);

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).age).isEqualTo(17);
    assertThat(list.get(1).age).isEqualTo(7);
    assertThat(list.get(2).age).isEqualTo(2);

  }

  @Test
  public void getList_CheckFilteredList_ageAsc(){

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

    insertAccount(clientId, 456456f);
    insertAccount(clientId, 456f);
    insertAccount(clientId2,654.45f);
    insertAccount(clientId3,654.45f);

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    assertThat(list).hasSize(3);
    assertThat(list.get(0).age).isEqualTo(2);
    assertThat(list.get(1).age).isEqualTo(7);
    assertThat(list.get(2).age).isEqualTo(17);

  }
  @Test
  public void getSize_ofFilteredList() {

    ClientListRequest req = new ClientListRequest();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllCharms();

    req.filterByFio  = "a";

    String charmId = RND.str(10);
    insertCharm(charmId);

    for (int i = 0; i < 50; i++) {
      String clientId = RND.str(10);

      clientTestDao.get().insertClient(
        clientId,
        "a" + RND.str(10),
        "a" + RND.str(10),
        "a" + RND.str(10),
        java.sql.Date.valueOf("1990-10-10"),
        "male",
        charmId
      );

      insertAccount(clientId, 265.841f);
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

  public void deleteAll(){
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
  }

  public void insertCharm(String charmId) {
    clientTestDao.get().insertCharm(charmId, RND.str(10));
  }

  public void insertClient(String clientId, String charmId) {
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

  public void insertClientWithDate(String clientId, String charmId, String date){
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

  public void insertAccount(String clientId, float money) {
    clientTestDao.get().insertClientAccount(
      RND.str(10),
      clientId,
      money,
      RND.str(5),
      new Date()
    );
  }


}