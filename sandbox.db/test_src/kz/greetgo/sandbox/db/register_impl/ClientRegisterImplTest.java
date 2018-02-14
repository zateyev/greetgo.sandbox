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

    clientTestDao.get().removeAllData();

    List<ClientDetails> clients = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
      clients.add(client);
    }

    //
    //
    long result = clientRegister.get().getTotalSize(null, null);
    //
    //

    assertThat(result).isEqualTo(clients.size());
  }

  @Test
  public void getTotalSize_filteredBySurname() {

    clientTestDao.get().removeAllData();

    List<ClientDetails> clients = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
      clients.add(client);
    }

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

    clientTestDao.get().removeAllData();

    List<ClientDetails> clients = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
      clients.add(client);
    }

    String filterInput = clients.get(RND.plusInt(clients.size())).name.toLowerCase().substring(5);
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
  public void getClientsList_default() {

    clientTestDao.get().removeAllData();

    List<ClientDetails> clients = new ArrayList<>();

    int page = 1;
    int pageSize = 10;

    for (int i = 0; i < 20; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
      clients.add(client);
    }

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> {
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
      expectingClientList.add(clientInfo);
    });

    expectingClientList.sort(Comparator.comparing(o -> o.surname));

    PageUtils.cutPage(expectingClientList, page * pageSize, pageSize);

    //
    //
    List<ClientInfo> result = clientRegister.get().getClientsList(null, null, null,
      null, page, pageSize);
    //
    //

    for (int i = 0; i < 20; i++) {
      assertThat(result).isNotNull();
      assertThat(result.get(i)).isEqualsToByComparingFields(expectingClientList.get(i));
    }
  }

  @Test
  public void getClientDetails_ok() {
    clientTestDao.get().removeAllData();

    List<ClientDetails> clients = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      ClientDetails client = createRndClient();
      clientTestDao.get().insertClientDot(client.id, client.surname, client.name,
        client.patronymic, client.gender, Date.valueOf(client.dateOfBirth), client.charm.name);
      clients.add(client);
    }

    ClientDetails expectedClient = clients.get(RND.plusInt(clients.size()));

    ClientDetails result = clientRegister.get().getClientDetails(expectedClient.id);

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectedClient);
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

    ClientInfo result = clientRegister.get().addOrUpdateClient(clientRecords);

    assertThat(result).isNotNull();
    assertThat(result).isEqualsToByComparingFields(expectedClientInfo);
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
}
