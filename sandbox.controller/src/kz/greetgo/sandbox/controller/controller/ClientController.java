package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

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
