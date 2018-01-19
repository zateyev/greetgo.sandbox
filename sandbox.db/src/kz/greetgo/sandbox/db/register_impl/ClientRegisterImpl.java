package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {
  @Override
  public long getCount(String nameFilter) {
    return 0;
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordListRequest listRequest) {
    return null;
  }

  @Override
  public void removeRecord(long id) {

  }

  @Override
  public ClientDetails getDetails(Long id) {
    return null;
  }

  @Override
  public void saveDetails(ClientDetailsToSave detailsToSave) {

  }
}
