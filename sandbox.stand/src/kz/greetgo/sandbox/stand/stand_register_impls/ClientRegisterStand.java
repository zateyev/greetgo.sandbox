package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandClientDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

@Bean
public class ClientRegisterStand implements ClientRegister {
  public BeanGetter<StandClientDb> al;

  @Override
  public ClientRecord[] getList(int page) {
    ClientRecord[] list = new ClientRecord[al.get().clientStorage.size()];
    int index = 0;
    for (ClientDot d : al.get().clientStorage.values()) {
      list[index] = d.toClientRecord();
      index++;
    }
    System.out.println(page);
    return list;
  }

  @Override
  public ClientDetails getClient(String id) {
    return al.get().clientStorage.get(id).toClientDetails();
  }


  int i = 50;

  @Override
  public ClientRecord saveClient(ClientToSave clientToSave) {
    i++;
    System.out.println(clientToSave.name);
    ClientDot clientDot = new ClientDot();
    if (clientToSave.id == null) {
      clientToSave.id = Integer.toString(i);
    }
    clientDot.id = clientToSave.id;
    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    al.get().clientStorage.put(clientToSave.id, clientDot);

    System.out.println(al.get().clientStorage.get(clientToSave.id).name + " id   " + clientToSave.id + "    i " + i);
    return al.get().clientStorage.get(clientToSave.id).toClientRecord();
  }

  @Override
  public void deleteClient(String id) {
    al.get().clientStorage.remove(id);
  }
}
