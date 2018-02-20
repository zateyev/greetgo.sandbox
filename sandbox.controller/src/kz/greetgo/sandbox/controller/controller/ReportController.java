package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ReportView;

import javax.lang.model.type.UnknownTypeException;
import java.io.OutputStream;

public class ReportController {
  public BeanGetter<ReportRegister> reportRegister;

  @Mapping("/report/{viewType}/{clientId}")
  public void downloadReport(@ParPath("viewType") String viewType,
                             @ParPath("clientId") String contractId,
                             RequestTunnel tunnel) throws Exception {
    String clientId = "asd"; // got from session

    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = result." + viewType);
    OutputStream out = tunnel.getResponseOutputStream();

//    ReportView view = getView(viewType, out);
    reportRegister.get().genReport(clientId, contractId, viewType, out);
    tunnel.flushBuffer();
  }
}
