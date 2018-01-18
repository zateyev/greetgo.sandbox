package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;
import java.util.Map;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/pageCount")
  public long pageCount(@Par("clientRecordCount") long clientRecordCount, @Par("clientRecordNameFilter") String clientRecordNameFilter) {
    return clientRegister.get().getPageCount(clientRecordCount, clientRecordNameFilter);
  }

  @ToJson
  @Mapping("/list")
  public List<ClientRecord> clientRecordList(@Par("clientRecordListRequest") @Json ClientRecordListRequest clientRecordListRequest) {
    return clientRegister.get().getClientRecordList(clientRecordListRequest);
  }

  @Mapping("/remove")
  public void removeClientDetails(@Par("clientRecordId") long clientRecordId) {
    clientRegister.get().removeClientDetails(clientRecordId);
  }

  @ToJson
  @Mapping("/details")
  public ClientDetails getClientDetails(@Par("clientRecordId") Long clientRecordId) {
    return clientRegister.get().getClientDetails(clientRecordId);
  }

  @Mapping("/save")
  public void saveClientDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave clientDetailsToSave) {
    clientRegister.get().saveClientDetails(clientDetailsToSave);
  }
}
