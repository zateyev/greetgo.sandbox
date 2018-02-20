package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.BigReportRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportView;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportViewPdf;

import javax.lang.model.type.UnknownTypeException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ReportController {
  public BeanGetter<ReportRegister> reportRegister;
  public BeanGetter<BigReportRegister> bigReportRegister;

  @Mapping("/report/{viewType}/{clientId}")
  public void downloadReport(@ParPath("viewType") String viewType,
                             @ParPath("clientId") String contractId,
                             RequestTunnel tunnel) throws Exception {
    String clientId = "asd"; // got from session

    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = result." + viewType);
    OutputStream out = tunnel.getResponseOutputStream();

//    ReportView view = getView(viewType, out);
//    reportRegister.get().genReport(clientId, contractId, viewType, out);
    tunnel.flushBuffer();
  }

  @Mapping("/big_report/pdf")
  public void downloadBigReport(@Par("filterBy") String filterBy,
                                @Par("filterInputs") String filterInputs,
                                @Par("orderBy") String orderBy,
                                @Par("isDesc") boolean isDesc,
//                                @ParPath("viewType") String viewType,
                                RequestTunnel tunnel) throws Exception {

    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = BigReport.txt"/* + viewType*/);
    OutputStream out = tunnel.getResponseOutputStream();

    PrintStream printStream = new PrintStream(out, false, "UTF-8");

//    ReportView view = getView(viewType, out);
//    bigReportRegister.get().genReport(clientId, viewType, printStream);

    BigReportViewPdf viewPdf = new BigReportViewPdf(printStream);
    bigReportRegister.get().genReport(filterBy, filterInputs, orderBy, isDesc, viewPdf);

    printStream.flush();
    tunnel.flushBuffer();
  }
}
