package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandClientDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

@Bean
public class ClientRegisterStand implements ClientRegister {
  public BeanGetter<StandClientDb> al;
  @Override
  public ClientRecord[] getList(int page){
    ClientRecord[] list = new ClientRecord[al.get().clientStorage.size()];
    int index = 0;
    for(ClientDot d: al.get().clientStorage.values()){
      list[index] = d.toClientRecord();
      index++;
    }
    System.out.println(page);
    return list;
  }

  @Override
  public ClientDetails getClient(String id) {
    return al.get().clStorage.get(id).toClientDetails();
  }

  @Override
  public void saveClient(String id, String json) {
    System.out.println(json);
  }

  @Override
  public void deleteClient(String id){
    al.get().clientStorage.remove(id);
  }
}
