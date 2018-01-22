package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  BeanGetter<ClientDao> clientDao;

  @Override
  public long getCount(String nameFilter) {
    if (nameFilter == null) throw new InvalidParameter();

    long recordCount = clientDao.get().selectEnabledWithFilterCountTableClient(nameFilter);

    return recordCount;
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
