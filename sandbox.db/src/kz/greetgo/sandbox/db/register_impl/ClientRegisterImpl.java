package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecords;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.util.RND;

import java.sql.Date;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public long getTotalSize(String filterBy, String filterInput) {
//    return clientDao.get().getTotalSize(filterBy != null ? filterBy : "surname",
//      filterInput != null ? filterInput : "");

    return 0;
  }

  @Override
  public List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, boolean isDesc, int page, int pageSize) {
    return null;
  }

  @Override
  public ClientDetails getClientDetails(String clientId) {
    ClientDetails clientDetails = clientDao.get().selectClientDetailsById(clientId);
    if (clientDetails == null) throw new NotFound();
    return clientDetails;
  }

  @Override
  public ClientInfo addOrUpdateClient(ClientRecords clientRecords) {
    clientDao.get().insertOrUpdateClient(clientRecords.id, clientRecords.surname, clientRecords.name,
      clientRecords.patronymic, clientRecords.gender, Date.valueOf(clientRecords.dateOfBirth), clientRecords.charm.id);
//    clientDao.get().insertOrUpdateClient(clientRecords);
    return clientDao.get().selectClientInfoById(clientRecords.id);
  }

  @Override
  public void removeClient(String clientsId) {
//    clientDao.get().removeById(clientsId);
//    throw new NotFound();
  }
}
