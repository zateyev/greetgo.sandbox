package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ReportView;
import kz.greetgo.sandbox.db.report.client_list.ReportViewPdf;
import kz.greetgo.sandbox.db.report.client_list.ReportViewXlsx;

import java.io.OutputStream;

@Bean
public class ReportRegisterImpl implements ReportRegister {

  @Override
  public void genReport(String clientId, String contractId, String viewType, OutputStream out) throws Exception {
    ClientInfo inData = getInDataFromDb(contractId, contractId);
    ReportView view = getView(viewType, out);
    view.generate(inData);
  }

  private ClientInfo getInDataFromDb(String userId, String contractId) {
    ClientInfo ret = new ClientInfo();
    ret.surname = userId;
    return ret;
  }

  private ReportView getView(String viewType, OutputStream out) {
    switch (viewType) {
      case "pdf":
        return new ReportViewPdf(out);

      case "xlsx":
        return new ReportViewXlsx(out);
    }
    throw new RuntimeException("Unknown type: " + viewType);
  }
}
