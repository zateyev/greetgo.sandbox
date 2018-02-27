package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.PageUtils;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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

    RequestParameters requestParams = new RequestParameters();

    //
    //
    long result = clientRegister.get().getTotalSize(requestParams);
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

    RequestParameters requestParams = new RequestParameters();
    requestParams.filterBy = "surname";
    requestParams.filterInput = filterInput;

    //
    //
    long result = clientRegister.get().getTotalSize(requestParams);
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

    RequestParameters requestParams = new RequestParameters();
    requestParams.filterBy = "name";
    requestParams.filterInput = filterInput;

    //
    //
    long result = clientRegister.get().getTotalSize(requestParams);
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

    RequestParameters requestParams = new RequestParameters();
    requestParams.filterBy = "patronymic";
    requestParams.filterInput = filterInput;

    //
    //
    long result = clientRegister.get().getTotalSize(requestParams);
    //
    //

    assertThat(result).isEqualTo(count);
  }

  @Test
  public void getClientsList_default() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    expectingClientList.sort(Comparator.comparing(o -> o.surname.toLowerCase()));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByAge() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    //TODO и не забываем про лямбды
    expectingClientList.sort((o1, o2) -> {

      Integer tb1 = o1.age;
      Integer tb2 = o2.age;
      int sComp = tb1.compareTo(tb2);

      if (sComp != 0) {
        return sComp;
      } else {
        String sn1 = o1.surname != null ? o1.surname.toLowerCase() : "";
        String sn2 = o2.surname != null ? o2.surname.toLowerCase() : "";
        return sn1.compareTo(sn2);
      }
    });

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.orderBy = "age";

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  private void assertThatAreEqual(ClientInfo ci1, ClientInfo ci2) {
    assertThat(ci1.id).isEqualTo(ci2.id);
    assertThat(ci1.surname).isEqualTo(ci2.surname);
    assertThat(ci1.name).isEqualTo(ci2.name);
    assertThat(ci1.patronymic).isEqualTo(ci2.patronymic);
    assertThat(ci1.charm.id).isEqualTo(ci2.charm.id);
    assertThat(ci1.age).isEqualTo(ci2.age);
    assertThat(Math.abs(ci1.totalBalance - ci2.totalBalance)).isLessThan(0.001);
    assertThat(Math.abs(ci1.minBalance - ci2.minBalance)).isLessThan(0.001);
    assertThat(Math.abs(ci1.maxBalance - ci2.maxBalance)).isLessThan(0.001);
  }

  @Test
  public void getClientsList_orderedByTotalBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    //TODO здесь можно использовать стримы
//    List<ClientInfo> expectingClientList = new ArrayList<>();
//    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    //TODO ...вот так:
    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    //TODO и не забываем про лямбды
    expectingClientList.sort((o1, o2) -> {

      Double tb1 = o1.totalBalance;
      Double tb2 = o2.totalBalance;
      int sComp = tb1.compareTo(tb2);

      if (sComp != 0) {
        return sComp;
      } else {
        String sn1 = o1.surname != null ? o1.surname.toLowerCase() : "";
        String sn2 = o2.surname != null ? o2.surname.toLowerCase() : "";
        return sn1.compareTo(sn2);//TODO а если sn1 == null ?
      }
    });

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.orderBy = "totalBalance";

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_descOrderedByTotalBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    expectingClientList.sort((o1, o2) -> {

      Double tb1 = o1.totalBalance;
      Double tb2 = o2.totalBalance;
      int sComp = tb2.compareTo(tb1);

      if (sComp != 0) {
        return sComp;
      } else {
        String sn1 = o1.surname != null ? o1.surname.toLowerCase() : "";
        String sn2 = o2.surname != null ? o2.surname.toLowerCase() : "";
        return sn1.compareTo(sn2);
      }
    });

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.orderBy = "totalBalance";
    requestParams.isDesc = true;

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByMinBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    expectingClientList.sort((o1, o2) -> {

      Double tb1 = o1.minBalance;
      Double tb2 = o2.minBalance;
      int sComp = tb1.compareTo(tb2);

      if (sComp != 0) {
        return sComp;
      } else {
        String sn1 = o1.surname != null ? o1.surname.toLowerCase() : "";
        String sn2 = o2.surname != null ? o2.surname.toLowerCase() : "";
        return sn1.compareTo(sn2);
      }
    });

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.orderBy = "minBalance";

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_orderedByMaxBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    int pageSize = RND.plusInt(clients.size());
    int page = pageSize > 0 ? RND.plusInt((int) Math.ceil(clients.size() / pageSize)) : 0;

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    expectingClientList.sort((o1, o2) -> {

      Double tb1 = o1.maxBalance;
      Double tb2 = o2.maxBalance;
      int sComp = tb1.compareTo(tb2);

      if (sComp != 0) {
        return sComp;
      } else {
        String sn1 = o1.surname != null ? o1.surname.toLowerCase() : "";
        String sn2 = o2.surname != null ? o2.surname.toLowerCase() : "";
        return sn1.compareTo(sn2);
      }
    });

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.orderBy = "maxBalance";

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredBySurname() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> clientInfos = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    String filterInput = clients.get(RND.plusInt(clients.size())).surname.toLowerCase().substring(7);

    List<ClientInfo> expectingClientList = filterClientList(clientInfos, "surname", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(expectingClientList.size());
    int page = 0;
    if (pageSize > 0) {
      page = RND.plusInt((int) Math.ceil(expectingClientList.size() / pageSize));
    } else {
      pageSize = expectingClientList.size();
    }

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.filterBy = "surname";
    requestParams.filterInput = filterInput;

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByName() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> clientInfos = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    String filterInput = clients.get(RND.plusInt(clients.size())).name.toLowerCase().substring(7);

    List<ClientInfo> expectingClientList = filterClientList(clientInfos, "name", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(expectingClientList.size());
    int page = 0;
    if (pageSize > 0) {
      page = RND.plusInt((int) Math.ceil(expectingClientList.size() / pageSize));
    } else {
      pageSize = expectingClientList.size();
    }

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.filterBy = "name";
    requestParams.filterInput = filterInput;

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByPatronymic() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> clientInfos = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);

    List<ClientInfo> expectingClientList = filterClientList(clientInfos, "patronymic", filterInput);

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    int pageSize = RND.plusInt(expectingClientList.size());
    int page = 0;
    if (pageSize > 0) {
      page = RND.plusInt((int) Math.ceil(expectingClientList.size() / pageSize));
    } else {
      pageSize = expectingClientList.size();
    }

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.filterBy = "patronymic";
    requestParams.filterInput = filterInput;

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
    }
  }

  @Test
  public void getClientsList_filteredByPatronymicAndOrderedByMinBalance() {

    List<ClientDetails> clients = clearDbAndInsertTestData(200);

    List<ClientInfo> clientInfos = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    String filterInput = clients.get(RND.plusInt(clients.size())).patronymic.toLowerCase().substring(7);

    List<ClientInfo> expectingClientList = filterClientList(clientInfos, "patronymic", filterInput);

    expectingClientList.sort(Comparator.comparingDouble(o -> o.minBalance));

    int pageSize = RND.plusInt(expectingClientList.size());
    int page = 0;
    if (pageSize > 0) {
      page = RND.plusInt((int) Math.ceil(expectingClientList.size() / pageSize));
    } else {
      pageSize = expectingClientList.size();
    }

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    RequestParameters requestParams = new RequestParameters(page, pageSize);
    requestParams.filterBy = "patronymic";
    requestParams.filterInput = filterInput;
    requestParams.orderBy = "minBalance";

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(requestParams);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(expectingClientList.size());
    for (int i = 0; i < expectingClientList.size(); i++) {
      assertThatAreEqual(result.get(i), expectingClientList.get(i));
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
    assertThatAreEqual(result, expectingClient);
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
    ClientRecordsToSave clientRecordsToSave = toClientRecords(clientDetails);

    charmTestDao.get().insertCharm(clientRecordsToSave.charm.id, clientRecordsToSave.charm.name,
      clientRecordsToSave.charm.description, clientRecordsToSave.charm.energy);

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecordsToSave);
    //
    //

    assertThat(result).isNotNull();
    assertThatAreEqual(result, expectingClientInfo);

    ClientDetails actual = clientTestDao.get().getClientDetailsById(clientRecordsToSave.id);
    assertThat(actual).isNotNull();
    actual.addressF = clientTestDao.get().getAddrByClientId(clientRecordsToSave.id, AddressType.FACT);
    actual.addressR = clientTestDao.get().getAddrByClientId(clientRecordsToSave.id, AddressType.REG);
    actual.phoneNumbers = clientTestDao.get().getPhonesByClientId(clientRecordsToSave.id);
    assertThatAreEqual(actual, clientDetails);
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
    ClientRecordsToSave clientRecordsToSave = toClientRecords(clientDetails);

    charmTestDao.get().insertCharm(clientRecordsToSave.charm.id, clientRecordsToSave.charm.name,
      clientRecordsToSave.charm.description, clientRecordsToSave.charm.energy);

    //
    //
    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecordsToSave);
    //
    //

    assertThat(result).isNotNull();
    assertThatAreEqual(result, expectingClientInfo);

    ClientDetails actual = clientTestDao.get().getClientDetailsById(clientRecordsToSave.id);
    assertThat(actual).isNotNull();
    actual.addressF = clientTestDao.get().getAddrByClientId(clientRecordsToSave.id, AddressType.FACT);
    actual.addressR = clientTestDao.get().getAddrByClientId(clientRecordsToSave.id, AddressType.REG);
    actual.phoneNumbers = clientTestDao.get().getPhonesByClientId(clientRecordsToSave.id);
    assertThatAreEqual(actual, clientDetails);
  }

  @Test
  public void removeClient_ok() {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();

    charmTestDao.get().insertCharm(client.charm.id, client.charm.name,
      client.charm.description, client.charm.energy);

    clientTestDao.get().insertClient(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.id);

    clientTestDao.get().insertAddress(client.id, client.addressF.type, client.addressF.street, client.addressF.house,
      client.addressF.flat);

    assertThat(client).isNotNull();

    //
    //
    clientRegister.get().removeClient(client.id);
    //
    //

    assertThat(clientTestDao.get().getClientDetailsById(client.id)).isNull();
  }

  @Test
  public void removeClient_NotFound() throws Exception {
    clientTestDao.get().removeAllData();

    ClientDetails client = createRndClient();

    charmTestDao.get().insertCharm(client.charm.id, client.charm.name,
      client.charm.description, client.charm.energy);

    clientTestDao.get().insertClient(client.id, client.surname, client.name,
      client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.id);

    assertThat(client).isNotNull();

    //
    //
    clientRegister.get().removeClient(idGen.get().newId());
    //
    //

    assertThat(clientTestDao.get().getClientDetailsById(client.id)).isNotNull();
  }

  private List<ClientDetails> clearDbAndInsertTestData(int size) {
    clientTestDao.get().removeAllData();
    List<ClientDetails> clients = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertCharm(client.charm.id, client.charm.name, client.charm.description, client.charm.energy);
      clientTestDao.get().insertClient(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.id);
      clientTestDao.get().insertAddress(client.id, client.addressF.type, client.addressF.street, client.addressF.house, client.addressF.flat);
      clientTestDao.get().insertAddress(client.id, client.addressR.type, client.addressR.street, client.addressR.house, client.addressR.flat);
      for (PhoneNumber phoneNumber : client.phoneNumbers) {
        clientTestDao.get().insertPhoneNumber(client.id, phoneNumber.number, phoneNumber.phoneType);
      }
//      for (int j = 0; j < client.phoneNumbers.size(); j++) {
//        clientTestDao.get().insertPhoneNumber(client.id, client.phoneNumbers.get(j).number, client.phoneNumbers.get(j).phoneType);
//      }
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
    client.surname = (10000 + RND.plusInt(99999)) + RND.str(5);
    client.name = RND.str(10);
    client.patronymic = RND.str(10);
    client.charm = new Charm();
    client.charm.id = idGen.get().newId();
    client.charm.name = RND.str(10);
    client.charm.description = RND.str(10);
    client.charm.energy = RND.plusDouble(100, 2);
    client.gender = RND.someEnum(Gender.values());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    client.dateOfBirth = sdf.format(RND.dateDays(-20_000, 0));
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

  private List<ClientInfo> filterClientList(List<ClientInfo> clientInfos, String filterBy, String filterInput) {
    List<ClientInfo> clientList = new ArrayList<>();
    for (ClientInfo clientInfo : clientInfos) {
      if ("surname".equals(filterBy) && clientInfo.surname.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
      else if ("name".equals(filterBy) && clientInfo.name.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
      else if ("patronymic".equals(filterBy) && clientInfo.patronymic.toLowerCase().contains(filterInput.toLowerCase()))
        clientList.add(clientInfo);
    }
    return clientList;
  }

  private ClientInfo toClientInfo(ClientDetails clientDetails) {
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.id = clientDetails.id;
    clientInfo.surname = clientDetails.surname;
    clientInfo.name = clientDetails.name;
    clientInfo.patronymic = clientDetails.patronymic;
    clientInfo.charm = clientDetails.charm;
    clientInfo.age = clientDetails.dateOfBirth != null ? Period.between(LocalDate.parse(clientDetails.dateOfBirth),
      LocalDate.now()).getYears() : 0;
    clientInfo.totalBalance = clientDetails.totalBalance;
    clientInfo.minBalance = clientDetails.minBalance;
    clientInfo.maxBalance = clientDetails.maxBalance;
    return clientInfo;
  }

  private ClientRecordsToSave toClientRecords(ClientDetails clientDetails) {
    ClientRecordsToSave clientRecordsToSave = new ClientRecordsToSave();
    clientRecordsToSave.id = clientDetails.id;
    clientRecordsToSave.surname = clientDetails.surname;
    clientRecordsToSave.name = clientDetails.name;
    clientRecordsToSave.patronymic = clientDetails.patronymic;
    clientRecordsToSave.charm = clientDetails.charm;
    clientRecordsToSave.gender = clientDetails.gender;
    clientRecordsToSave.dateOfBirth = clientDetails.dateOfBirth;
    clientRecordsToSave.addressF = clientDetails.addressF;
    clientRecordsToSave.addressR = clientDetails.addressR;
    clientRecordsToSave.phoneNumbers = clientDetails.phoneNumbers;
    clientRecordsToSave.totalBalance = clientDetails.totalBalance;
    clientRecordsToSave.minBalance = clientDetails.minBalance;
    clientRecordsToSave.maxBalance = clientDetails.maxBalance;
    return clientRecordsToSave;
  }

  private void assertThatAreEqual(ClientDetails cd1, ClientDetails cd2) {
    assertThat(cd1.id).isEqualTo(cd2.id);
    assertThat(cd1.surname).isEqualTo(cd2.surname);
    assertThat(cd1.name).isEqualTo(cd2.name);
    assertThat(cd1.patronymic).isEqualTo(cd2.patronymic);
    assertThat(cd1.gender).isEqualTo(cd2.gender);
    assertThat(cd1.dateOfBirth).isEqualTo(cd2.dateOfBirth);
    assertThat(cd1.charm.id).isEqualTo(cd2.charm.id);
    assertThat(cd1.addressF.street).isEqualTo(cd2.addressF.street);
    assertThat(cd1.addressF.house).isEqualTo(cd2.addressF.house);
    assertThat(cd1.addressF.flat).isEqualTo(cd2.addressF.flat);
    assertThat(cd1.addressR.street).isEqualTo(cd2.addressR.street);
    assertThat(cd1.addressR.house).isEqualTo(cd2.addressR.house);
    assertThat(cd1.addressR.flat).isEqualTo(cd2.addressR.flat);
    cd1.phoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number.toLowerCase()));
    cd2.phoneNumbers.sort(Comparator.comparing(phoneNumber -> phoneNumber.number.toLowerCase()));
    assertThat(cd1.phoneNumbers).hasSize(cd2.phoneNumbers.size());
    for (int i = 0; i < cd1.phoneNumbers.size(); i++) {
      assertThat(cd1.phoneNumbers.get(i).number).isEqualTo(cd2.phoneNumbers.get(i).number);
      assertThat(cd1.phoneNumbers.get(i).phoneType).isEqualTo(cd2.phoneNumbers.get(i).phoneType);
    }
    assertThat(cd1.charm.id).isEqualTo(cd2.charm.id);
  }
}
