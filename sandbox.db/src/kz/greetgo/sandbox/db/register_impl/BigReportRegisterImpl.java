package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportFootData;
import kz.greetgo.sandbox.controller.report.ReportHeadData;
import kz.greetgo.sandbox.db.jdbc.BigReportJdbc;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportViewPdf;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.PrintStream;
import java.util.Date;

@Bean
public class BigReportRegisterImpl implements BigReportRegister {

  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<ClientRegister> clientRegister;

  @Override
  public void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, BigReportView view) {

    ReportHeadData head = new ReportHeadData();
    head.title = "Список клиентов";

    view.start(head);

    jdbcSandbox.get().execute(new BigReportJdbc("", "", "", false, 0, 0, view));

    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();

    view.finish(foot);
  }
}
