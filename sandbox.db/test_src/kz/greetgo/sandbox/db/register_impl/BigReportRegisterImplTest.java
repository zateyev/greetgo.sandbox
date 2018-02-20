package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportFootData;
import kz.greetgo.sandbox.controller.report.ReportHeadData;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BigReportRegisterImplTest {

  public BeanGetter<BigReportRegister> bigReportRegister;

  private static class TestReportView implements BigReportView {

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
    // insert into db test data
    String clientId = "asd";
    Date from = null;
    Date to = null;
    TestReportView testReportView = new TestReportView();

    //
    //
    bigReportRegister.get().genReport(clientId, testReportView);
    //
    //

    assertThat(testReportView.headData).isNotNull();
    assertThat(testReportView.headData.title).isEqualTo("asfsf");
    assertThat(testReportView.footData).isNotNull();

    assertThat(testReportView.clientList).hasSize(10);
    assertThat(testReportView.clientList.get(3).id).isEqualTo("asf");
    assertThat(testReportView.clientList.get(3).surname).isEqualTo("asf");
    assertThat(testReportView.clientList.get(3).name).isEqualTo("asf");
  }
}