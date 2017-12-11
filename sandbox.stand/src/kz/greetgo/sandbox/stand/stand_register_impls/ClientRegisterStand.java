package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandClientDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
  public BeanGetter<StandClientDb> al;

  @Override
  public long getSize() {
    return al.get().clientStorage.size();
  }

  @Override
  public List<ClientRecord> getList(int page, String sort) {
    ClientRecord[] fullList = new ClientRecord[al.get().clientStorage.size()];
    int index = 0;
    for (ClientDot d : al.get().clientStorage.values()) {
      fullList[index] = d.toClientRecord();
      index++;
    }

    int listSize = 5;
    int begin = listSize * page;
    int end = begin + listSize;
    if (end > al.get().clientStorage.size()) listSize = al.get().clientStorage.size() % listSize;

//    System.out.println(listSize);
    List<ClientRecord> list = new ArrayList<>();
//    for (int i = 0; i < list.length; i++) {
//      list.add(fullList[begin]);
//      begin++;
//    }
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
    ClientDot clientDot = new ClientDot();
    if (clientToSave.id == null) clientToSave.id = Integer.toString(i);
    else clientDot = al.get().clientStorage.get(clientToSave.id);


////////////////////////////////////////////////////////////////////////////////
    clientDot.id = clientToSave.id;
    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    clientDot.gender = clientToSave.gender;
    clientDot.temper = clientToSave.temper;
    clientDot.dateOfBirth = clientToSave.dateOfBirth;

    String str = "";
    String str2 = "";
    for (String address : clientToSave.firstAddress) str = str + " " + address;
    for (String phones : clientToSave.phones) str2 = str2 + " " + phones;
    clientDot.address = str.trim();
    clientDot.phone = str2.trim();
//////////////////////////////////////////////////////////////////////////////////
    al.get().clientStorage.put(clientToSave.id, clientDot);

    System.out.println(al.get().clientStorage.get(clientToSave.id).temper + " id   " + clientToSave.id + "    i " + i);
    return al.get().clientStorage.get(clientToSave.id).toClientRecord();
  }

  @Override
  public void deleteClient(String id) {
    al.get().clientStorage.remove(id);
  }
}
