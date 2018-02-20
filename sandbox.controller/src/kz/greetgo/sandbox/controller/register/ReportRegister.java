package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.report.ReportView;

import java.io.OutputStream;

public interface ReportRegister {
  void genReport(String clientId, String contractId, ReportView view/*, String view, OutputStream out*/) throws Exception;
}
