package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ClientListInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/list")
  public List<ClientListInfo> list(@Par("page") Integer page, @Par("size") Integer size) {
    return clientRegister.get().getClientList(page, size);
  }

  @ToJson
  @Mapping("/pageNum")
  public Integer pageNum(@Par("size") Integer size) {
    return clientRegister.get().getPageNum(size);
  }
}
