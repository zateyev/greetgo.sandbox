package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.jdbc.LoadClientList;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.sandbox.db.util.PageUtils;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGen;

  @Test
  public void getTotalSize_noFilter() {

    List<ClientDetails> clients = clearDbAndInsertTestData(100);

    assertThat(clients).isNotNull();

    //
    //
    long result = clientRegister.get().getTotalSize("", "");
    //
    //

    assertThat(result).isEqualTo(clients.size());
  }

  @Test
  public void getTotalSize_filteredBySurname() {

    List<ClientDetails> clients = clearDbAndInsertTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).surname.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.surname.toLowerCase().contains(filterInput)) count++;
    }

    //
    //
    long result = clientRegister.get().getTotalSize("surname", filterInput);
    //
    //

    assertThat(result).isEqualTo(count);
  }

  @Test
  public void getTotalSize_filteredByName() {

    List<ClientDetails> clients = clearDbAndInsertTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).name.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.name.toLowerCase().contains(filterInput)) count++;
    }

    //
    //
    long result = clientRegister.get().getTotalSize("name", filterInput);
    //
    //

    assertThat(result).isEqualTo(count);
  }

  @Test
  public void getTotalSize_filteredByPatronymic() {

    List<ClientDetails> clients = clearDbAndInsertTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.patronymic.toLowerCase().contains(filterInput)) count++;
    }

    //
    //
    long result = clientRegister.get().getTotalSize("patronymic", filterInput);
    //
    //

    assertThat(result).isEqualTo(count);
  }

  @Test
  public void getClientsList_default() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

//    int pageSize = RND.plusInt(clients.size());
//    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));
    int pageSize = 25;
    int page = 1;

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.surname.toLowerCase()));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("", "", "",
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i).id).isEqualTo(expectingClientList.get(i).id);
      assertThat(result.get(i).surname).isEqualTo(expectingClientList.get(i).surname);
      assertThat(result.get(i).name).isEqualTo(expectingClientList.get(i).name);
      assertThat(result.get(i).patronymic).isEqualTo(expectingClientList.get(i).patronymic);
      assertThat(result.get(i).charm.id).isEqualTo(expectingClientList.get(i).charm.id);
      assertThat(result.get(i).age).isEqualTo(expectingClientList.get(i).age);
      assertThat(Math.abs(result.get(i).totalBalance - expectingClientList.get(i).totalBalance)).isLessThan(0.001);
      assertThat(Math.abs(result.get(i).minBalance - expectingClientList.get(i).minBalance)).isLessThan(0.001);
      assertThat(Math.abs(result.get(i).maxBalance - expectingClientList.get(i).maxBalance)).isLessThan(0.001);
    }
  }

  @Test
  public void getClientsList_orderedByAge() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparingInt(o -> o.age));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, "age",
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByTotalBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparingDouble(o -> o.totalBalance));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, "totalBalance",
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_descOrderedByTotalBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparingDouble(o -> o.totalBalance));
    Collections.reverse(expectingClientList);

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("", "", "totalBalance",
      true, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByMinBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparingDouble(o -> o.minBalance));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, "minBalance",
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByMaxBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparingDouble(o -> o.maxBalance));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, "maxBalance",
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredBySurname() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    String filterInput = clients.get(RND.plusInt(clients.size())).surname.toLowerCase().substring(7);

    filterClientList(expectingClientList, "surname", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));
    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("surname", filterInput, null,
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByName() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    String filterInput = clients.get(RND.plusInt(clients.size())).name.toLowerCase().substring(7);

    filterClientList(expectingClientList, "name", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));
    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("name", filterInput, null,
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByPatronymic() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);

    filterClientList(expectingClientList, "patronymic", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));
    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("patronymic", filterInput, null,
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByPatronymicAndOrderedByMinBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);

    filterClientList(expectingClientList, "patronymic", filterInput);

    expectingClientList.sort(Comparator.comparingDouble(o -> o.minBalance));

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));
    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList("patronymic", filterInput, null,
      false, page, pageSize);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientDetails_ok() {

    List<ClientDetails> clients = clearDbAndInsertTestData(10);

    ClientDetails expectingClient = clients.get(RND.plusInt(clients.size()));

    //
    //
    ClientDetails result = clientRegister.get().getClientDetails(expectingClient.id);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectingClient);
  }

  @Test(expectedExceptions = NotFound.class)
  public void getClientDetails_NotFound() throws Exception {

    List<ClientDetails> clients = clearDbAndInsertTestData(10);

    //
    //
    ClientDetails result = clientRegister.get().getClientDetails(idGen.get().newId());
    //
    //
  }

  @Test
  public void addOrUpdateClient_add() {
    clientTestDao.get().removeAllData();


    ClientDetails clientDetails = createRndClient();
    ClientInfo expectingClientInfo = toClientInfo(clientDetails);
    ClientRecords clientRecords = toClientRecords(clientDetails);

    charmTestDao.get().insertCharm(clientRecords.charm.id, clientRecords.charm.name,
      clientRecords.charm.description, clientRecords.charm.energy);

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecords);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectingClientInfo);
  }

  @Test
  public void addOrUpdateClient_update() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();

    assertThat(client).isNotNull();

    charmTestDao.get().insertCharm(client.charm.id, client.charm.name,
      client.charm.description, client.charm.energy);

    clientTestDao.get().insertClient(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.id);

    ClientDetails clientDetails = createRndClient();
    clientDetails.id = client.id;

    ClientInfo expectingClientInfo = toClientInfo(clientDetails);
    ClientRecords clientRecords = toClientRecords(clientDetails);

    charmTestDao.get().insertCharm(clientRecords.charm.id, clientRecords.charm.name,
      clientRecords.charm.description, clientRecords.charm.energy);

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecords);
    //
    //

    assertThat(result).isNotNull();
    // TODO: 2/19/18 isEqualsToByComparingFields gives incorrect statement
    assertThat(result).isEqualsToByComparingFields(expectingClientInfo);
  }

  @Test
  public void removeClient_ok() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();
    clientTestDao.get().insertClient(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);

    assertThat(client).isNotNull();

    //
    //
    clientRegister.get().removeClient(client.id);
    //
    //

    assertThat(clientTestDao.get().getClientById(client.id)).isNull();
  }

  @Test
  public void removeClient_NotFound() throws Exception {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();
    clientTestDao.get().insertClient(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);

    assertThat(client).isNotNull();

    //
    //
    clientRegister.get().removeClient(idGen.get().newId());
    //
    //

    assertThat(clientTestDao.get().getClientById(client.id)).isNotNull();
  }

  private List<ClientDetails> clearDbAndInsertTestData(int size) {
    clientTestDao.get().removeAllData();
    List<ClientDetails> clients = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertCharm(client.charm.id, client.charm.name, client.charm.description, client.charm.energy);
      clientTestDao.get().insertClient(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.id);
      // TODO: 2/16/18 type of registeredAt timestamp should be OffsetDateTime
      double total = 0.0;
      double min = 1000.0;
      double max = 0.0;
      for (int j = 0; j < RND.plusInt(4); j++) {
        double money = RND.plusDouble(1000, 2);
        total += money;
        if (money < min) min = money;
        if (money > max) max = money;
        clientTestDao.get().insertClientAccount(idGen.get().newId(), client.id, money,
          RND.str(10), null);
      }
      client.totalBalance = total;
      client.minBalance = min < 1000.0 ? min : 0.0;
      client.maxBalance = max;
      clients.add(client);
    }
    return clients;
  }

  private ClientDetails createRndClient() {
    ClientDetails client = new ClientDetails();
    client.id = idGen.get().newId();
    client.surname = RND.str(10);
    client.name = RND.str(10);
    client.patronymic = RND.str(10);
    client.charm = new Charm();
    client.charm.id = idGen.get().newId();
    client.charm.name = RND.str(10);
    client.charm.description = RND.str(10);
    client.charm.energy = RND.plusDouble(100, 2);
    client.gender = RND.someEnum(Gender.values());
    client.dateOfBirth = LocalDate.now().toString();
    client.addressF = new Address();
    client.addressF.type = AddressType.FACT;
    client.addressF.street = RND.str(10);
    client.addressF.house = RND.str(5);
    client.addressF.flat = RND.str(5);
    client.addressR = new Address();
    client.addressR.type = AddressType.REG;
    client.addressR.street = RND.str(10);
    client.addressR.house = RND.str(5);
    client.addressR.flat = RND.str(5);
    for (int i = 0; i < RND.plusInt(2) + 3; i++) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.phoneType = RND.someEnum(PhoneType.values());
      phoneNumber.number = RND.str(10);
      client.phoneNumbers.add(phoneNumber);
    }
    return client;
  }

  private void filterClientList(List<ClientInfo> clientInfos, String filterBy, String filterInput) {
    List<ClientInfo> clientList = new ArrayList<>();
    for (ClientInfo clientInfo : clientInfos) {
      if ("surname".equals(filterBy) && clientInfo.surname.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
      else if ("name".equals(filterBy) && clientInfo.name.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
      else if ("patronymic".equals(filterBy) && clientInfo.patronymic.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
    }
    clientInfos = clientList;
  }

  private ClientInfo toClientInfo(ClientDetails clientDetails) {
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.id = clientDetails.id;
    clientInfo.surname = clientDetails.surname;
    clientInfo.name = clientDetails.name;
    clientInfo.patronymic = clientDetails.patronymic;
    clientInfo.charm = clientDetails.charm;
    clientInfo.age = Period.between(LocalDate.parse(clientDetails.dateOfBirth), LocalDate.now()).getYears();
    clientInfo.totalBalance = clientDetails.totalBalance;
    clientInfo.minBalance = clientDetails.minBalance;
    clientInfo.maxBalance = clientDetails.maxBalance;
    return clientInfo;
  }

  private ClientRecords toClientRecords(ClientDetails clientDetails) {
    ClientRecords clientRecords = new ClientRecords();
    clientRecords.id = clientDetails.id;
    clientRecords.surname = clientDetails.surname;
    clientRecords.name = clientDetails.name;
    clientRecords.patronymic = clientDetails.patronymic;
    clientRecords.charm = clientDetails.charm;
    clientRecords.gender = clientDetails.gender;
    clientRecords.dateOfBirth = clientDetails.dateOfBirth;
    clientRecords.addressF = clientDetails.addressF;
    clientRecords.addressR = clientDetails.addressR;
    clientRecords.phoneNumbers = clientDetails.phoneNumbers;
    clientRecords.totalBalance = clientDetails.totalBalance;
    clientRecords.minBalance = clientDetails.minBalance;
    clientRecords.maxBalance = clientDetails.maxBalance;
    return clientRecords;
  }
}
