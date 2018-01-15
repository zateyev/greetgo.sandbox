package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;
import java.util.Map;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/init/charm")
  public Map<Integer, List<String>> initCharmData() {
    return clientRegister.get().getCharmData();
  }

  @ToJson
  @Mapping("/pageCount")
  public long pageCount(@Par("clientRecordCount") long clientRecordCount) {
    return clientRegister.get().getPageCount(clientRecordCount);
  }

  @ToJson
  @Mapping("/list")
  public List<ClientRecord> clientRecordList(@Par("clientRecordCountToSkip") long clientRecordCountToSkip, @Par("clientRecordCount") long clientRecordCount) {
    return clientRegister.get().getClientRecordList(clientRecordCountToSkip, clientRecordCount);
  }

  @ToJson
  @Mapping("/remove")
  public boolean removeClientRecord(@Par("clientRecordId") long clientRecordId) {
    return clientRegister.get().removeClientRecord(clientRecordId);
  }
}
