package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandClientDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
  public BeanGetter<StandClientDb> al;

  @Override
  public long getSize(ClientListRequest clientListRequest) {
    if("".equals(clientListRequest.filterByFio)) {
      return al.get().clientStorage.size();
    }
    else return 5;
  }



  @Override
  public List<ClientRecord> getList(ClientListRequest clientListRequest) {
    List<ClientDot> fullList = new ArrayList(al.get().clientStorage.values());
    List<ClientRecord> list = new ArrayList<>();

    System.out.println("List Info" + clientListRequest.count + " " + clientListRequest.sort);

    for (int i = clientListRequest.skipFirst; i < clientListRequest.count; i++) {
      list.add(fullList.get(i).toClientRecord());
      if (clientListRequest.count > fullList.size()) break;
    }

    ClientRecord nn = fullList.get(3).toClientRecord();

    List<ClientRecord> list2 = new ArrayList<>();
    list2.add(nn);


    if("".equals(clientListRequest.filterByFio)) return list;
    else return list2;
  }

  @Override
  public ClientDetails getClient(String id) {
    if (id != null) return al.get().clientStorage.get(id).toClientDetails();
    else {
      ClientDetails det = new ClientDetails();
      det.charms = al.get().clientStorage.get("1").toClientDetails().charms;
      return det;
    }
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
    clientDot.temper = clientToSave.charmId;
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

  @Override
  public void getFile(ClientListRequest clientListRequest) {
    File file = new File("sandbox.stand/db/src/kz/greetgo/sandbox/db/stand/beans/pdf.pdf");
    file.getParentFile().mkdirs();
    try {
      Files.copy(file.toPath(),
        (new File("sandbox.client/build/public/files/myfile.pdf")).toPath(),
        StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      System.out.println("File dont created");
      e.printStackTrace();
    }
  }
}
