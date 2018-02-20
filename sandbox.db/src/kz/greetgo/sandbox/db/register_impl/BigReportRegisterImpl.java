package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportFootData;
import kz.greetgo.sandbox.controller.report.ReportHeadData;
import kz.greetgo.sandbox.db.jdbc.BigReportJdbc;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BigReportRegisterImpl implements BigReportRegister {

  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<ClientRegister> clientRegister;

  @Override
  public void genReport(String clientId, BigReportView view) {
    ReportHeadData head = new ReportHeadData();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    head.title = "Список клиентов";
    view.start(head);

    jdbcSandbox.get().execute(new BigReportJdbc("", "", "", false, 0, 0, view));

    String surname = clientRegister.get().getClientDetails(clientId).surname;

    ReportFootData foot = new ReportFootData();
    foot.generatedBy = surname;
    foot.generatedAt = new Date();
    view.finish(foot);
  }
}
