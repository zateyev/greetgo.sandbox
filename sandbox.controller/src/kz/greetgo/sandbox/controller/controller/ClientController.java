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

import static kz.greetgo.mvc.core.RequestMethod.DELETE;
import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(GET)
  @Mapping("/count")
  public long getCount(@Par("clientRecordNameFilter") String nameFilter) {
    return clientRegister.get().getCount(nameFilter);
  }

  @ToJson
  @MethodFilter(GET)
  @Mapping("/list")
  public List<ClientRecord> getRecordList(@Par("clientRecordListRequest") @Json ClientRecordListRequest listRequest) {
    return clientRegister.get().getRecordList(listRequest);
  }

  @MethodFilter(DELETE)
  @Mapping("/remove")
  public void removeRecord(@Par("clientRecordId") long id) {
    clientRegister.get().removeRecord(id);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/details")
  public ClientDetails getDetails(@Par("clientRecordId") Long id) {
    return clientRegister.get().getDetails(id);
  }

  @MethodFilter(POST)
  @Mapping("/save")
  public void saveDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave detailsToSave) {
    clientRegister.get().saveDetails(detailsToSave);
  }
}
