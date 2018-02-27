package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.jdbc.LoadClientListToReport;
import kz.greetgo.sandbox.db.report.client_list.ReportFootData;
import kz.greetgo.sandbox.db.report.client_list.ReportHeadData;
import kz.greetgo.sandbox.db.report.client_list.big_data.ReportView;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ReportRegisterImpl}
 */
public class ReportRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGen;
  public BeanGetter<JdbcSandbox> jdbcSandbox;

  private static class TestReportView implements ReportView {

    public ReportHeadData headData = null;
    public ReportFootData footData = null;

    @Override
    public void start(ReportHeadData headData) {

      this.headData = headData;
    }

    public final List<ClientInfo> clientList = new ArrayList<>();

    @Override
    public void addRow(ClientInfo row) {
      clientList.add(row);
    }

    @Override
    public void finish(ReportFootData footData) {

      this.footData = footData;
    }
  }

  @Test
  public void genReport() throws Exception {
    List<ClientDetails> clients = clearDbAndInsertTestData(50);

    List<ClientInfo> expectingClientList = clients.stream()
      .map(this::toClientInfo)
      .collect(Collectors.toList());

    expectingClientList.sort(Comparator.comparing(o -> o.surname.toLowerCase()));

    TestReportView testReportView = new TestReportView();


    //
    //
    ReportHeadData head = new ReportHeadData();
    head.title = "Список клиентов";
    testReportView.start(head);
    jdbcSandbox.get().execute(new LoadClientListToReport("", "", "", false, 0, 0, testReportView));
    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();
    testReportView.finish(foot);
    //
    //

    assertThat(testReportView.headData).isNotNull();
    assertThat(testReportView.headData.title).isEqualTo("Список клиентов");
    assertThat(testReportView.footData).isNotNull();

    assertThat(testReportView.clientList).hasSize(50);
    assertThat(testReportView.clientList.get(3).id).isEqualTo(expectingClientList.get(3).id);
    assertThat(testReportView.clientList.get(3).surname).isEqualTo(expectingClientList.get(3).surname);
    assertThat(testReportView.clientList.get(3).name).isEqualTo(expectingClientList.get(3).name);
  }

  @Test
  public void genReport_orderedByMinBalance() throws Exception {
    List<ClientDetails> clients = clearDbAndInsertTestData(50);

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
        String sn1 = o1.surname.toLowerCase();
        String sn2 = o2.surname.toLowerCase();
        return sn1.compareTo(sn2);
      }
    });

    TestReportView testReportView = new TestReportView();

    //
    //
    ReportHeadData head = new ReportHeadData();
    head.title = "Список клиентов";
    testReportView.start(head);
    jdbcSandbox.get().execute(new LoadClientListToReport("", "", "minBalance", false, 0, 0, testReportView));
    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();
    testReportView.finish(foot);
    //
    //

    assertThat(testReportView.headData).isNotNull();
    assertThat(testReportView.headData.title).isEqualTo("Список клиентов");
    assertThat(testReportView.footData).isNotNull();

    assertThat(testReportView.clientList).hasSize(expectingClientList.size());
    int rndInd = RND.plusInt(expectingClientList.size());
    assertThat(testReportView.clientList.get(rndInd).id).isEqualTo(expectingClientList.get(rndInd).id);
    assertThat(testReportView.clientList.get(rndInd).surname).isEqualTo(expectingClientList.get(rndInd).surname);
    assertThat(testReportView.clientList.get(rndInd).name).isEqualTo(expectingClientList.get(rndInd).name);
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
        client.patronymic, client.gender, java.sql.Date.valueOf(client.dateOfBirth), client.charm.id);
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