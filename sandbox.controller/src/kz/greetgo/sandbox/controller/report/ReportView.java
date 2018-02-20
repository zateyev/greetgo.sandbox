package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientInfo;

public interface ReportView {
  void generate(ClientInfo clientInfo) throws Exception;
}
