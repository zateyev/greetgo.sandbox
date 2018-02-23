package kz.greetgo.sandbox.db.report.client_list;

import kz.greetgo.sandbox.controller.model.ClientInfo;

public interface ReportView {
  void generate(ClientInfo clientInfo) throws Exception;
}
