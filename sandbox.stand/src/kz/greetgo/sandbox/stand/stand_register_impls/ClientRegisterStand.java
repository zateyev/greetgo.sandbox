package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.util.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public long getPageCount(long clientRecordCount) {
    long ret = db.get().clientStorage.size() / clientRecordCount;
    if (db.get().clientStorage.size() % clientRecordCount > 0)
      ret++;

    return ret;
  }

  @Override
  public List<ClientRecord> getClientRecordList(ClientRecordListRequest clientRecordListRequest) {
    List<ClientDot> clientDots;
    List<ClientRecord> clientRecords = new ArrayList<>();

    System.out.println(clientRecordListRequest.columnSortType);
    System.out.println(clientRecordListRequest.sortAscend);

    switch (clientRecordListRequest.columnSortType) {
      case AGE:
        clientDots = this.getListByAge(clientRecordListRequest.sortAscend);
        break;
      case TOTALACCOUNTBALANCE:
        clientDots = this.getListByTotalAccountBalance(clientRecordListRequest.sortAscend);
        break;
      case MAXACCOUNTBALANCE:
        clientDots = this.getListByMaxAccountBalance(clientRecordListRequest.sortAscend);
        break;
      case MINACCOUNTBALANCE:
        clientDots = this.getListByMinAccountBalance(clientRecordListRequest.sortAscend);
        break;
      default:
        clientDots = this.getDefaultList();
    }

    PageUtils.cutPage(clientDots,
      clientRecordListRequest.clientRecordCountToSkip,
      clientRecordListRequest.clientRecordCount);

    for (ClientDot clientDot:clientDots)
      clientRecords.add(clientDot.toClientRecord());

    return clientRecords;
  }

  private List<ClientDot> getDefaultList() {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());

    return clientDots;
  }

  private List<ClientDot> getListByAge(boolean ascend) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());

    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o1.age - o2.age;
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o2.age - o1.age;
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByTotalAccountBalance(boolean ascend) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());

    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.totalAccountBalance).compareTo(o2.totalAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.totalAccountBalance).compareTo(o1.totalAccountBalance);
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByMaxAccountBalance(boolean ascend) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());

    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.maxAccountBalance).compareTo(o2.maxAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.maxAccountBalance).compareTo(o1.maxAccountBalance);
        }
      });
    }

    return clientDots;
  }


  private List<ClientDot> getListByMinAccountBalance(boolean ascend) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());

    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.minAccountBalance).compareTo(o2.minAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.minAccountBalance).compareTo(o1.minAccountBalance);
        }
      });
    }

    return clientDots;
  }

  @Override
  public boolean removeClientRecord(long clientRecordId) {
    Map<Long, ClientDot> clientDots = db.get().clientStorage;

    if (clientDots.remove(clientRecordId) != null)
      return true;

    return false;
  }
}
