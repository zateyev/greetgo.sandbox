package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecords;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public long getTotalSize(String filterBy, String filterInput) {
    return clientDao.get().getTotalSize(filterBy != null ? filterBy : "surname",
      filterInput != null ? filterInput : "");
  }

  @Override
  public List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, String isDesc, int page, int pageSize) {
    return null;
  }

  @Override
  public ClientDetails getClientDetails(String clientId) {
    return null;
  }

  @Override
  public ClientInfo addOrUpdateClient(ClientRecords clientRecords) {
    return null;
  }

  @Override
  public void removeClient(String clientsId) {

  }
}
