package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.RequestParameters;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ViewType;
import kz.greetgo.sandbox.db.jdbc.LoadClientListToReport;
import kz.greetgo.sandbox.db.report.client_list.ReportFootData;
import kz.greetgo.sandbox.db.report.client_list.ReportHeadData;
import kz.greetgo.sandbox.db.report.client_list.big_data.ReportView;
import kz.greetgo.sandbox.db.report.client_list.big_data.ReportViewPdf;
import kz.greetgo.sandbox.db.report.client_list.big_data.ReportViewXlsx;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.OutputStream;
import java.util.Date;

@Bean
public class ReportRegisterImpl implements ReportRegister {

  public BeanGetter<JdbcSandbox> jdbcSandbox;

  @Override
  public void genReport(RequestParameters requestParams, ViewType viewType, OutputStream out) throws DocumentException {

    ReportHeadData head = new ReportHeadData();
    head.title = "Список клиентов";

    ReportView view = getView(viewType, out);

    view.start(head);

    jdbcSandbox.get().execute(new LoadClientListToReport(requestParams.filterBy,
      requestParams.filterInput,
      requestParams.orderBy,
      requestParams.isDesc,
      0,
      0,
      view
    ));

    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();

    view.finish(foot);
  }

  private ReportView getView(ViewType viewType, OutputStream out) {
    switch (viewType) {
      case PDF:
        return new ReportViewPdf(out);
      case XLSX:
        return new ReportViewXlsx(out);
    }
    throw new RuntimeException("View type not found");
  }
}
