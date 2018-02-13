package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
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
  public long getTotalSize(@Par("filterBy") String filterBy,
                           @Par("filterInputs") String filterInputs) {
    return clientRegister.get().getTotalSize(filterBy, filterInputs);
  }

  @ToJson
  @Mapping("/clientsList")
  public List<ClientInfo> clientsList(@Par("filterBy") String filterBy,
                                      @Par("filterInputs") String filterInputs,
                                      @Par("orderBy") String orderBy,
                                      @Par("isDesc") String isDesc,
                                      @Par("page") int page,
                                      @Par("pageSize") int pageSize) {
    return clientRegister.get().getClientsList(filterBy, filterInputs, orderBy, isDesc, page, pageSize);
  }

  @ToJson
  @Mapping("/clientDetails")
  public ClientDetails getClientDetails(@Par("clientsId") String clientsId) {
    return clientRegister.get().getClientDetails(clientsId);
  }

  @ToJson
  @Mapping("/addOrUpdateClient")
  public ClientInfo addClient(@Par("clientRecords") @Json ClientRecords clientRecords) {
    return clientRegister.get().addOrUpdateClient(clientRecords);
  }

  @ToJson
  @Mapping("/removeClient")
  public void removeClient(@Par("clientsId") String clientsId,
                           @Par("page") int page,
                           @Par("pageSize") int pageSize) {
    clientRegister.get().removeClient(clientsId, page, pageSize);
  }
}
