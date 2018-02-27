package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.RequestParameters;
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
  public void downloadReport(@Par("requestParams") @Json RequestParameters requestParams,
                             @ParPath("viewType") String viewType,
                             RequestTunnel tunnel) throws Exception {

    tunnel.setResponseHeader("Content-Disposition", "attachment; filename = report." + viewType);
    OutputStream out = tunnel.getResponseOutputStream();

    reportRegister.get().genReport(requestParams, ViewType.valueOf(viewType.toUpperCase()), out);

    tunnel.flushBuffer();
  }
}
