package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ViewType;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/report")
public class ReportController implements Controller {
  public BeanGetter<ReportRegister> reportRegister;

  @ToJson
  @Mapping("/{viewType}")
  public void downloadReport(@Par("filterBy") String filterBy,
                             @Par("filterInputs") String filterInputs,
                             @Par("orderBy") String orderBy,
                             @Par("isDesc") boolean isDesc,
                             @ParPath("viewType") String viewType,
                             RequestTunnel tunnel) throws Exception {

//    tunnel.setResponseContentType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = BigReport." + viewType);
    OutputStream out = tunnel.getResponseOutputStream();

    reportRegister.get().genReport(filterBy, filterInputs, orderBy, isDesc, ViewType.valueOf(viewType.toUpperCase()), out);

    tunnel.flushBuffer();
  }
}
