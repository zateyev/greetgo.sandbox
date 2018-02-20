package kz.greetgo.sandbox.controller.register;

import java.io.OutputStream;

public interface ReportRegister {
  void genReport(String clientId, String contractId, String view, OutputStream out) throws Exception;
}
