package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.CharmType;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public Map<Integer, List<String>> getCharmData() {
    return db.get().charmStorage;
  }

  @Override
  public long getPageCount(long clientRecordCount) {
    long ret = db.get().clientStorage.size() / clientRecordCount;
    if (db.get().clientStorage.size() % clientRecordCount > 0)
      ret++;

    return ret;
  }

  @Override
  public List<ClientRecord> getClientRecordList(long clientRecordCountToSkip, long clientRecordCount) {
    Map<Long, ClientDot> clientDots = db.get().clientStorage;
    Map<Integer, List<String>> charms = db.get().charmStorage;
    List<ClientRecord> clientRecords = new ArrayList<>();
    long skippedCount = 0L;
    long pushedCount = 0L;

    System.out.println(clientDots.size());

    //TODO: Метод PageUtils.cutPage требует полный список и будет заменен, т. к. оптимальнее будет брать кусок непосредственно с базы
    for (Map.Entry<Long, ClientDot> entry : clientDots.entrySet()) {
      if (skippedCount < clientRecordCountToSkip) {
        skippedCount++;
        continue;
      }

      if (pushedCount < clientRecordCount && pushedCount + skippedCount < clientDots.size()) {
        ClientDot clientDot = entry.getValue();
        ClientRecord clientRecord = clientDot.toClientRecord();
        clientRecord.charm = charms.get(clientDot.charm.ordinal()).get(clientDot.gender.ordinal());
        clientRecords.add(clientRecord);

        pushedCount++;
      } else
        break;
    }

    return clientRecords;
  }

  @Override
  public boolean removeClientRecord(long clientRecordId) {
    Map<Long, ClientDot> clientDots = db.get().clientStorage;

    if (clientDots.remove(clientRecordId) != null)
      return true;

    return false;
  }
}
