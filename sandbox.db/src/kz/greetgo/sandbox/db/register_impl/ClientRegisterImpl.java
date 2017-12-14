package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {
  @Override
  public long getSize(ClientListRequest clientListRequest) {
    throw new UnsupportedOperationException();
  }

  public BeanGetter<ClientDao> clientDao;

  @Override
  public List<ClientRecord> getList(ClientListRequest clientListRequest) {
    throw new UnsupportedOperationException();
  }


  @Override
  public ClientDetails getClient(String id) {
    ClientDetails ret = new ClientDetails();

    if (id != null && !"".equals(id)) {
      ret = clientDao.get().loadDetails(id);
    }
    ret.charms = clientDao.get().loadCharmList();

    return ret;
  }

  public BeanGetter<IdGenerator> idGen;

  @Override
  public ClientRecord saveClient(ClientToSave clientToSave) {
    if (clientToSave.id != null) {
      clientDao.get().updateClientField(clientToSave.id, "name", clientToSave.name);
      clientDao.get().updateClientField(clientToSave.id, "surname", clientToSave.surname);
      clientDao.get().updateClientField(clientToSave.id, "patronymic", clientToSave.patronymic);
      clientDao.get().updateClientField(clientToSave.id, "charm_id", clientToSave.charmId);
      clientDao.get().updateClientField(clientToSave.id, "actual", "1");
    } else {
      clientToSave.id = idGen.get().newId();
      clientDao.get().insertClient(
        clientToSave.id,
        clientToSave.name,
        clientToSave.surname,
        clientToSave.patronymic,
        clientToSave.charmId);
    }
    ClientRecord rec = new ClientRecord();

    rec.id = clientToSave.id;
    return rec;
  }

  @Override
  public void deleteClient(String id) {
    clientDao.get().deleteClient(id);
  }
}
