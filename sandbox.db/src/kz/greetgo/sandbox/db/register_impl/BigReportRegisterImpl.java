package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.msoffice.docx.Run;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ViewType;
import kz.greetgo.sandbox.db.report.client_list.ReportFootData;
import kz.greetgo.sandbox.db.report.client_list.ReportHeadData;
import kz.greetgo.sandbox.db.jdbc.BigReportJdbc;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportView;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportViewPdf;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportViewXlsx;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

@Bean
public class BigReportRegisterImpl implements BigReportRegister {

  public BeanGetter<JdbcSandbox> jdbcSandbox;

  @Override
  public void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, ViewType viewType, OutputStream out) throws DocumentException {

    ReportHeadData head = new ReportHeadData();
    head.title = "Список клиентов";

    BigReportView view = getView(viewType, out);

    view.start(head);

    jdbcSandbox.get().execute(new BigReportJdbc("", "", "", false, 0, 0, view));

    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();

    view.finish(foot);
  }

  private BigReportView getView(ViewType viewType, OutputStream out) {
    switch (viewType) {
      case PDF:
        return new BigReportViewPdf(out);
      case XLSX:
        return new BigReportViewXlsx(out);
    }
    throw new RuntimeException("View type not found");
  }
}
