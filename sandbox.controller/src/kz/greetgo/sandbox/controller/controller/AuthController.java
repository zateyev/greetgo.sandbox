package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/auth")
public class AuthController implements Controller {

  public BeanGetter<AuthRegister> authRegister;
  public BeanGetter<ClientRegister> clientRegister;

  @AsIs
  @NoSecurity
  @Mapping("/login")
  public String login(@Par("accountName") String accountName, @Par("password") String password) {
    return authRegister.get().login(accountName, password);
  }

  @ToJson
  @Mapping("/info")
  public AuthInfo info(@ParSession("personId") String personId) {
    return authRegister.get().getAuthInfo(personId);
  }

  @ToJson
  @Mapping("/userInfo")
  public UserInfo userInfo(@ParSession("personId") String personId) {
    return authRegister.get().getUserInfo(personId);
  }

  @ToJson
  @Mapping("/clientsList")
  public ClientsListInfo clientsList(@Par("page") int page, @Par("pageSize") int pageSize) {
    return clientRegister.get().getClientsList(page, pageSize);
  }

  @ToJson
  @Mapping("/clientsFullInfo")
  public ClientsFullInfo clientsFullInfo(@Par("clientsId") String clientsId) {
    return clientRegister.get().getClientsFullInfo(clientsId);
  }

  @ToJson
  @Mapping("/filterClientsList")
  public ClientsListInfo filterClientsList(@Par("filtersInput") String filtersInput,
                                           @Par("filterBy") String filterBy,
                                           @Par("page") int page,
                                           @Par("pageSize") int pageSize) {
    return clientRegister.get().filterClientsList(filtersInput, filterBy, page, pageSize);
  }

  @ToJson
  @Mapping("/sortClientsList")
  public ClientsListInfo sortClientsList(@Par("sortBy") String sortBy,
                                         @Par("desc") String desc,
                                         @Par("page") int page,
                                         @Par("pageSize") int pageSize) {
    return clientRegister.get().sortClientsList(sortBy, desc, page, pageSize);
  }

  @ToJson
  @Mapping("/addNewClient")
  public ClientInfo addNewClient(@Par("newClient") String newClient) {
    return clientRegister.get().addNewClient(newClient);
  }

  @ToJson
  @Mapping("/updateClient")
  public ClientInfo updateClient(@Par("clientParams") String clientParams) {
    return clientRegister.get().updateClient(clientParams);
  }

  @ToJson
  @Mapping("/removeClient")
  public void removeClient(@Par("clientsId") String clientsId,
                                      @Par("page") int page,
                                      @Par("pageSize") int pageSize) {
    clientRegister.get().removeClient(clientsId, page, pageSize);
  }
}
