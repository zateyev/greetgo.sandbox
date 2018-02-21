package kz.greetgo.sandbox.controller.register;

public interface ReportRegister {
  void genReport(String clientId, String contractId/*, String view, OutputStream out*/) throws Exception;
}
