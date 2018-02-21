package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ViewType;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;

@Bean
@Mapping("/report")
public class ReportController implements Controller {
  public BeanGetter<ReportRegister> reportRegister;
  public BeanGetter<BigReportRegister> bigReportRegister;

//  @Mapping("/{viewType}/{clientId}")
//  public void downloadReport(@ParPath("viewType") String viewType,
//                             @ParPath("clientId") String contractId,
//                             RequestTunnel tunnel) throws Exception {
//    String clientId = "asd"; // got from session
//
//    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = result." + viewType);
//    OutputStream out = tunnel.getResponseOutputStream();
//
//    tunnel.flushBuffer();
//  }

  @ToJson
  @Mapping("/xlsx")
  public void downloadBigReport(@Par("filterBy") String filterBy,
                                @Par("filterInputs") String filterInputs,
                                @Par("orderBy") String orderBy,
                                @Par("isDesc") boolean isDesc,
                                @ParPath("viewType") String viewType,
                                RequestTunnel tunnel) throws Exception {

    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = BigReport.xlsx"/* + viewType*/);
    OutputStream out = tunnel.getResponseOutputStream();

    System.out.println(ViewType.valueOf(viewType));

//    bigReportRegister.get().genReport(filterBy, filterInputs, orderBy, isDesc, ViewType.valueOf(viewType), out);
    bigReportRegister.get().genReport(filterBy, filterInputs, orderBy, isDesc, ViewType.XLSX, out);

    tunnel.flushBuffer();
  }
}
