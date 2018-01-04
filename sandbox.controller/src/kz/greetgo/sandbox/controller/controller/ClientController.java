package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.util.List;


@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/getSize")
  public long getSize(
    @Par("filter") @Json ClientListRequest clientListRequest) {
    return clientRegister.get().getSize(clientListRequest);
  }

  @ToJson
  @Mapping("/getList")
  public List<ClientRecord> getList(
    @Par("listInfo")
    @Json ClientListRequest clientListRequest) {
    return clientRegister.get().getList(clientListRequest);
  }

  @ToJson
  @Mapping("/getClient")
  public ClientDetails getClient(@Par("id") String id) {
    return clientRegister.get().getClient(id);
  }

  @Mapping("/download")
  public void download(@Par("listInfo") @Json ClientListRequest clientListRequest,
                       @Par("contentType") String contentType,
                       @ParSession("personId") String personId,
                       RequestTunnel tunnel
  ) throws Exception {

    tunnel.setResponseContentType(contentType);

    OutputStream outputStream = tunnel.getResponseOutputStream();

    if (contentType.contains("pdf")) {
      tunnel.setResponseHeader("content-disposition", "attachment; filename=\"asd.pdf\"");
    } else {
      tunnel.setResponseHeader("content-disposition", "attachment; filename=\"asd.xlsx\"");
    }
    clientRegister.get().download(clientListRequest, outputStream, contentType, personId);

    tunnel.flushBuffer();

  }

  @ToJson
  @Mapping("/saveClient")
  public ClientRecord saveClient(
    @Par("clientToSave") @Json ClientToSave clientToSave) {
    return clientRegister.get().saveClient(clientToSave);
  }

  @Mapping("/deleteClient")
  public void deleteClient(
    @Par("id") String id) {
    clientRegister.get().deleteClient(id);
  }
}
