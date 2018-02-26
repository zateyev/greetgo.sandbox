package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.RequestParameters;
import kz.greetgo.sandbox.controller.report.ViewType;

import java.io.OutputStream;

public interface ReportRegister {
  void genReport(RequestParameters requestParams, ViewType viewType, OutputStream out) throws Exception;
}
