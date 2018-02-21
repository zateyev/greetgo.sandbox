package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.db.report.client_list.ReportView;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.*;

public class ReportRegisterImplTest {

  public BeanGetter<ReportRegister> reportRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGen;

  @Test
  public void genReport() throws Exception {

    List<ClientDetails> clients = clearDbAndInsertTestData(100);

    List<ClientInfo> expectingClientList = new ArrayList<>();
    clients.forEach(clientDetails -> expectingClientList.add(toClientInfo(clientDetails)));

    expectingClientList.sort(Comparator.comparing(o -> o.surname.toLowerCase()));

    final ClientInfo clientList[] = new ClientInfo[1];

    //
    //
//    reportRegister.get().genReport("", "", new ReportView() {
//      @Override
//      public void generate(ClientInfo clientInfo) throws Exception {
//        clientList[0] = clientInfo;
//      }
//    });
    //
    //

    assertThat(clientList[0].id).isNotNull();
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

}