package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public List<ClientRecord> getClientRecordList(int page, int size) {
    List<ClientRecord> clientRecords = new ArrayList<>();

    /*Map<Long, ClientDot> clientDots = db.get().clientStorage;

    for(long i = 0; i < clientDots.size(); i++) {
      System.out.println(clientDots.get(i));
    }*/

    Map<Long, ClientDot> clientDots = db.get().clientStorage;

    for(long i = 0; i < clientDots.size(); i++) {
      clientRecords.add(clientDots.get(i).toClientRecord());
    }

    return clientRecords;
  }

  @Override
  public int getPageCount(int size) {
    int ret = db.get().personStorage.size() / size;

    if (ret == 0) ret = 1;

    return ret;
  }
}
