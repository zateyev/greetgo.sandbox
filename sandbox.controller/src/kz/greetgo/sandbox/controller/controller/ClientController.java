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

import static kz.greetgo.mvc.core.RequestMethod.DELETE;
import static kz.greetgo.mvc.core.RequestMethod.GET;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(GET)
  @Mapping("/count")
  public long getCount(@Par("clientRecordNameFilter") String namefilter) {
    return clientRegister.get().getCount(namefilter);
  }

  @ToJson
  @MethodFilter(GET)
  @Mapping("/list")
  public List<ClientRecord> getRecordList(@Par("clientRecordListRequest") @Json ClientRecordListRequest listRequest) {
    return clientRegister.get().getRecordList(listRequest);
  }

  @MethodFilter(DELETE)
  @Mapping("/remove")
  public void removeClientDetails(@Par("clientRecordId") long id) {
    clientRegister.get().removeRecord(id);
  }

  @ToJson
  @Mapping("/details")
  public ClientDetails getDetails(@Par("clientRecordId") Long id) {
    return clientRegister.get().getDetails(id);
  }

  @Mapping("/save")
  public void saveClientDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave detailsToSave) {
    clientRegister.get().saveDetails(detailsToSave);
  }
}
