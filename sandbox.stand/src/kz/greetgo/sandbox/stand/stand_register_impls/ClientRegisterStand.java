package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandClientDb;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.util.RND;
import kz.greetgo.util.ServerUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
  public BeanGetter<StandClientDb> al;

  @Override
  public long getSize(ClientListRequest clientListRequest) {
    if (clientListRequest.filterByFio == null) {
      return al.get().clientStorage.size();
    } else return 0;
  }


  @Override
  public List<ClientRecord> getList(ClientListRequest clientListRequest) {
    List<ClientDot> fullList = new ArrayList(al.get().clientStorage.values());

    if (clientListRequest.filterByFio != null && !clientListRequest.filterByFio.trim().isEmpty()) {
      List<ClientRecord> filteredClientList = new ArrayList<>();
      String term = clientListRequest.filterByFio.trim();
      for (ClientDot clientDot : fullList) {
        if (clientDot.name.contains(term) || clientDot.surname.contains(term) || (clientDot.patronymic != null && clientDot.patronymic.contains(term))) {
          filteredClientList.add(clientDot.toClientRecord());
        }
      }

      return filteredClientList;
    }

    List<ClientRecord> list = new ArrayList<>();

    for (int i = clientListRequest.skipFirst; i < clientListRequest.skipFirst + clientListRequest.count; i++) {
      if (i >= fullList.size()) break;
      list.add(fullList.get(i).toClientRecord());
    }

    return list;
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


  @Override
  public ClientRecord saveClient(ClientToSave clientToSave) {
    ClientDot clientDot = new ClientDot();
    if (clientToSave.id == null) clientToSave.id = RND.str(5);
    else clientDot = al.get().clientStorage.get(clientToSave.id);


////////////////////////////////////////////////////////////////////////////////
    clientDot.id = clientToSave.id;
    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    clientDot.gender = clientToSave.gender;
    clientDot.charmId = clientToSave.charmId;
    clientDot.dateOfBirth = clientToSave.dateOfBirth;
    clientDot.homePhone = clientToSave.phones.home;
    clientDot.workPhone = clientToSave.phones.work;
    clientDot.mobilePhone = clientToSave.phones.mobile;
    clientDot.factFlat = clientToSave.factAddress.flat;
    clientDot.factHouse = clientToSave.factAddress.house;
    clientDot.factStreet = clientToSave.factAddress.street;
    clientDot.regFlat = clientToSave.regAddress.flat;
    clientDot.regHouse = clientToSave.regAddress.house;
    clientDot.regStreet = clientToSave.regAddress.street;


    al.get().clientStorage.put(clientToSave.id, clientDot);

    return al.get().clientStorage.get(clientToSave.id).toClientRecord();
  }

  @Override
  public void deleteClient(String id) {
    al.get().clientStorage.remove(id);
  }


  @Override
  public void getClientListForReport(ClientListRequest clientListRequest,
                                     OutputStream outputStream,
                                     String contentType,
                                     String personId) throws Exception {

    if (contentType.contains("pdf")) {
      try (InputStream in = StandDb.class.getResourceAsStream("getClientListReport.pdf")) {
        ServerUtil.copyStreamsAndCloseIn(in, outputStream);
      }

      return;
    }

    if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
      try (InputStream in = StandDb.class.getResourceAsStream("getClientListReport.xlsx")) {
        ServerUtil.copyStreamsAndCloseIn(in, outputStream);
      }

      return;
    }

    throw new RuntimeException("Unknown content type " + contentType);
  }
}
