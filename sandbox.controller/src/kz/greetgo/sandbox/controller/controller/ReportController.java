package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


@Bean
@Mapping("/report")
public class ReportController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @Mapping("/clientList")
  public void downloadClientList(@Par("listInfo") @Json ClientListRequest clientListRequest,
                             @Par("contentType") String contentType,
                             @ParSession("personId") String personId,
                             RequestTunnel tunnel
  ) throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String date = sdf.format(new Date());

    tunnel.setResponseContentType(contentType);

    OutputStream outputStream = tunnel.getResponseOutputStream();

    if (contentType.contains("pdf")) {
      tunnel.setResponseHeader("content-disposition", "attachment; filename=\"List_of_clients-" + date + ".pdf\"");
    } else {
      tunnel.setResponseHeader("content-disposition", "attachment; filename=\"List_of_clients-" + date + ".xlsx\"");
    }

    clientRegister.get().getClientListForReport(clientListRequest, outputStream, contentType, personId);

    tunnel.flushBuffer();

  }

}
