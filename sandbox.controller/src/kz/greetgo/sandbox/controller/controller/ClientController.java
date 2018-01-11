package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/list")
  public List<ClientRecord> list(@Par("clientRecordCountToSkip") int clientRecordCountToSkip, @Par("clientRecordCount") int clientRecordCount) {
    return clientRegister.get().getClientRecordList(clientRecordCountToSkip, clientRecordCount);
  }

  @ToJson
  @Mapping("/pageCount")
  public int pageNum(@Par("clientRecordCount") int clientRecordCount) {
    return clientRegister.get().getPageCount(clientRecordCount);
  }
}
