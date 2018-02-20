package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.report.BigReportView;

public interface BigReportRegister {
  void genReport(String clientId, BigReportView view);
}
