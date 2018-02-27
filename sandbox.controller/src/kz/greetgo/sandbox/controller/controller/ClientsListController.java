package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.RequestParameters;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/clientsList")
public class ClientsListController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/totalSize")
  //TODO передавать только один объект (такой же как и в ClientsListController.clientsList() )
  public long getTotalSize(@Par("requestParams") @Json RequestParameters requestParams) {
    return clientRegister.get().getTotalSize(requestParams);
  }

  @ToJson
  @Mapping("/clientsList")//TODO передавать только один объект
  public List<ClientInfo> clientsList(@Par("requestParams") @Json RequestParameters requestParams) {

    //TODO убрать эти условия в скобках
    return clientRegister.get().getClientsList(requestParams);
  }

  @ToJson
  @Mapping("/clientDetails")
  public ClientDetails getClientDetails(@Par("clientsId") String clientsId) {
    return clientRegister.get().getClientDetails(clientsId);
  }

  @ToJson
  @Mapping("/addOrUpdateClient")
  public ClientInfo addClient(@Par("clientRecordsToSave") @Json ClientRecordsToSave clientRecordsToSave) {
    return clientRegister.get().addOrUpdateClient(clientRecordsToSave);
  }

  @ToJson
  @Mapping("/removeClient")
  public void removeClient(@Par("clientsId") String clientsId) {
    clientRegister.get().removeClient(clientsId);
  }
}
