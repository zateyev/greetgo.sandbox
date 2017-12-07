package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;


@Bean
@Mapping("/client")
public class ClientController implements Controller{

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/getList")
  public ClientRecord[] getList(@Par("page") int page){
    return clientRegister.get().getList(page);
      }

  @ToJson
  @Mapping("/getClient")
  public ClientDetails getClient(@Par("id") String id){
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
    @Par("id") String id
  ){
    clientRegister.get().deleteClient(id);
  }
}
