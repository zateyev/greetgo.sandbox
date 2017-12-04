package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.AuthRegister;
/**
 * Created by jgolibzhan on 11/30/17.
 */
@Bean
@Mapping("/client")
public class ClientController implements Controller{

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/getList")
  public ClientRecord[] getList(){
    return clientRegister.get().getList();
      }

  @ToJson
  @Mapping("/getClient")
  public ClientDetails getClient(@Par("id") String id){
    return clientRegister.get().getClient(id);
  }

  @Mapping("/saveClient")
  public void saveClient(
    @Par("id") String id,
    @Par("json") String json)
  { }

  @Mapping("/deleteClient")
  public void deleteClient(
    @Par("id") String id
  ){
    clientRegister.get().deleteClient(id);
  }
}
