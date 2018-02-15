package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.PageUtils;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.LocalDate;
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
  public BeanGetter<IdGenerator> idGen;

  @Test
  public void getTotalSize_noFilter() {

    List<ClientDetails> clients = clearDbAndLoadTestData(100);

    assertThat(clients).isNotNull();

    //
    //
    long result = clientRegister.get().getTotalSize(null, null);
    //
    //

    assertThat(result).isEqualTo(clients.size());
  }

  @Test
  public void getTotalSize_filteredBySurname() {

    List<ClientDetails> clients = clearDbAndLoadTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).surname.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.surname.contains(filterInput)) count++;
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

    List<ClientDetails> clients = clearDbAndLoadTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).name.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.name.contains(filterInput)) count++;
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

    List<ClientDetails> clients = clearDbAndLoadTestData(100);

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);
    int count = 0;
    for (ClientDetails client : clients) {
      if (client.patronymic.contains(filterInput)) count++;
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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, null,
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
  public void getClientsList_orderedByAge() {

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.age));

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.totalBalance));

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.totalBalance));
    Collections.reverse(expectingClientList);

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, "totalBalance",
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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.minBalance));

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = RND.plusInt((int) Math.ceil(clients.size() / pageSize));

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.maxBalance));

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

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

    List<ClientDetails> clients = clearDbAndLoadTestData(200);

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
  public void getClientDetails_ok() {

    List<ClientDetails> clients = clearDbAndLoadTestData(10);

    ClientDetails expectingClient = clients.get(RND.plusInt(clients.size()));

    //
    //
    ClientDetails result = clientRegister.get().getClientDetails(expectingClient.id);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectingClient);
  }

  @Test
  public void getClientDetails_notFound() {

    List<ClientDetails> clients = clearDbAndLoadTestData(10);

    //
    //
    ClientDetails result = clientRegister.get().getClientDetails(idGen.get().newId());
    //
    //

    assertThat(result).isNull();
  }

  @Test
  public void addOrUpdateClient_add() {
    clientTestDao.get().removeAllData();

    ClientRecords clientRecords = new ClientRecords();
    clientRecords.id = idGen.get().newId();
    clientRecords.surname = RND.str(10);
    clientRecords.name = RND.str(10);
    clientRecords.patronymic = RND.str(10);
    clientRecords.charm = new Charm();
    clientRecords.charm.name = RND.str(10);
    clientRecords.gender = RND.someEnum(Gender.values());
    clientRecords.dateOfBirth = LocalDate.now().toString();

    ClientInfo expectedClientInfo = new ClientInfo();
    expectedClientInfo.id = clientRecords.id;
    expectedClientInfo.surname = clientRecords.surname;
    expectedClientInfo.name = clientRecords.name;
    expectedClientInfo.patronymic = clientRecords.patronymic;
    expectedClientInfo.charm = clientRecords.charm;
    expectedClientInfo.age = Period.between(LocalDate.parse(clientRecords.dateOfBirth), LocalDate.now()).getYears();
    expectedClientInfo.totalBalance = clientRecords.totalBalance;
    expectedClientInfo.minBalance = clientRecords.minBalance;
    expectedClientInfo.maxBalance = clientRecords.maxBalance;

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecords);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectedClientInfo);
  }

  @Test
  public void addOrUpdateClient_update() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();

    assertThat(client).isNotNull();

    clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);

    ClientDetails clientDetails = createRndClient();
    clientDetails.id = client.id;

    ClientInfo expectingClientInfo = toClientInfo(clientDetails);
    ClientRecords clientRecords = toClientRecords(clientDetails);

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecords);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectingClientInfo);
  }

  @Test
  public void removeClient_ok() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();
    clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
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
  public void removeClient_notFound() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();
    clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);

    assertThat(client).isNotNull();

    String clientId = idGen.get().newId();

    //
    //
    clientRegister.get().removeClient(clientId);
    //
    //

    // FIXME: 2/14/18 this is awkward
    assertThat(clientTestDao.get().getClientById(client.id)).isNotNull();
  }

  private List<ClientDetails> clearDbAndLoadTestData(int size) {
    clientTestDao.get().removeAllData();
    List<ClientDetails> clients = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
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
    client.charm.name = RND.str(10);
    client.gender = RND.someEnum(Gender.values());
    client.dateOfBirth = LocalDate.now().toString();
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
    ClientRecords clientInfo = new ClientRecords();
    clientInfo.id = clientDetails.id;
    clientInfo.surname = clientDetails.surname;
    clientInfo.name = clientDetails.name;
    clientInfo.patronymic = clientDetails.patronymic;
    clientInfo.charm = clientDetails.charm;
    clientInfo.dateOfBirth = clientDetails.dateOfBirth;
    clientInfo.totalBalance = clientDetails.totalBalance;
    clientInfo.minBalance = clientDetails.minBalance;
    clientInfo.maxBalance = clientDetails.maxBalance;
    return clientInfo;
  }
}
