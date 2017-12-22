package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

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

    assertThat(details.firstAddress).hasSize(3);
    assertThat(details.firstAddress.get(0)).isEqualTo("reg");
    assertThat(details.firstAddress.get(1)).isEqualTo("reg");
    assertThat(details.firstAddress.get(2)).isEqualTo("reg");

    assertThat(details.charms).hasSize(2);
    assertThat(details.charms.get(0).name).isEqualTo(charmName2);
    assertThat(details.charms.get(1).name).isEqualTo(charmName1);

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

    String address = RND.str(10);

    String charmId = RND.str(10);
    String charmName = RND.str(10);


    clientTestDao.get().insertCharm(charmId, charmName);

    cl.id = null;
    cl.name = RND.str(10);
    cl.surname = RND.str(10);
    cl.patronymic = RND.str(10);
    cl.charmId = charmId;
    cl.dateOfBirth = "1992-11-12";
    cl.gender = "male";
    cl.firstAddress.add(address);
    cl.firstAddress.add(address);
    //cl.firstAddress.add(address);

    //
    //
    ClientRecord rec = clientRegister.get().saveClient(cl);
    //
    //

    ClientDetails actual = clientTestDao.get().loadDetails(rec.id);

    List<String> adresses = clientTestDao.get().getFirstAddress(actual.id);

    assertThat(actual).isNotNull();
    assertThat(actual.name).isEqualTo(cl.name);
    assertThat(actual.surname).isEqualTo(cl.surname);
    assertThat(actual.patronymic).isEqualTo(cl.patronymic);
    assertThat(actual.charmId).isEqualTo(cl.charmId);
    assertThat(actual.dateOfBirth).isEqualTo(cl.dateOfBirth);
    assertThat(actual.gender).isEqualTo(cl.gender);
    assertThat(adresses.get(0)).isEqualTo(address);
    assertThat(adresses.get(1)).isEqualTo(address);
    assertThat(adresses.get(2)).isNullOrEmpty();

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

    clientTestDao.get().insertClient(
      clientId,
      name,
      surname,
      patronymic,
      java.sql.Date.valueOf("1990-10-10"),
      "male",
      charmId);

    ClientToSave clUpdated = new ClientToSave();
    clUpdated.id = clientId;
    clUpdated.name = name + "new";
    clUpdated.surname = surname + "new";
    clUpdated.patronymic = patronymic + "new";
    clUpdated.dateOfBirth = "1990-11-11";
    clUpdated.gender = "female";
    clUpdated.charmId = charmId;
    clUpdated.firstAddress.add("address new");
    clUpdated.firstAddress.add("address new");
    clUpdated.firstAddress.add("address new");

    //
    //
    ClientRecord rec = clientRegister.get().saveClient(clUpdated);
    //
    //

    ClientDetails actual = clientTestDao.get().loadDetails(rec.id);
    List<String> addresses = clientTestDao.get().getFirstAddress(clientId);

    assertThat(actual.name).isEqualTo(clUpdated.name);
    assertThat(actual.surname).isEqualTo(clUpdated.surname);
    assertThat(actual.patronymic).isEqualTo(clUpdated.patronymic);
    assertThat(actual.dateOfBirth).isEqualTo(clUpdated.dateOfBirth);
    assertThat(addresses.get(0)).isEqualTo(clUpdated.firstAddress.get(0));
    assertThat(addresses.get(1)).isEqualTo(clUpdated.firstAddress.get(1));
    assertThat(addresses.get(2)).isEqualTo(clUpdated.firstAddress.get(2));

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

  }

  @Test
  public void getList_CheckLimitedList() {
    String charmId = RND.str(5);
    String charmName = RND.str(10);
    clientTestDao.get().insertCharm(charmId, charmName);
    clientTestDao.get().deleteAllClients();
    String clientName = RND.str(10);

    for (int i = 0; i < 50; i++) {
      clientTestDao.get().insertClient(
        RND.str(10),
        clientName,
        RND.str(10),
        RND.str(10),
        java.sql.Date.valueOf("1990-10-10"),
        "male",
        charmId
      );
    }

    ClientListRequest req = new ClientListRequest();
    req.count = 5;
    req.skipFirst = 10;
    req.sort = "";
    req.filterByFio = "";

    //
    //
    List<ClientRecord> list = clientRegister.get().getList(req);
    //
    //

    List<String> detList = clientTestDao.get().getListOfIds();

    assertThat(detList.get(10)).isEqualTo(list.get(0).id);
    assertThat(list).hasSize(5);

  }

  @Test
  public void getSize() {

    ClientListRequest req = new ClientListRequest();
    clientTestDao.get().deleteAllClients();
    req.filterByFio = "";

    for (int i = 0; i < 50; i++) {
      clientTestDao.get().insertClient(
        RND.str(10),
        RND.str(10),
        RND.str(10),
        RND.str(10),
        java.sql.Date.valueOf("1990-10-10"),
        "male",
        null
      );
    }

    //
    //
    long size = clientRegister.get().getSize(req);
    //
    //

    assertThat(size).isEqualTo(50);

  }


  @Test
  public void loadTestData() {
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllCharms();

    String charmId = RND.str(5);
    String charmName = RND.str(10);
    clientTestDao.get().insertCharm(charmId, charmName);

    for (int i = 0; i < 50; i++) {
      ClientToSave save = new ClientToSave();
      save.name = RND.str(10);
      save.surname = RND.str(10);
      save.patronymic = RND.str(10);
      save.charmId = charmId;
      save.dateOfBirth = "1990-10-10";
      save.gender = "male";
      save.firstAddress.add("address");
      save.firstAddress.add("address");
      save.firstAddress.add("");

      ClientRecord rec = clientRegister.get().saveClient(save);
    }

  }



}