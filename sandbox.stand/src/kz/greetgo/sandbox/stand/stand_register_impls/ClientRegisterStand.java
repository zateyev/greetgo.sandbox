package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.ClientListInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public List<ClientListInfo> getClientList(Integer page, Integer size) {

    List<ClientListInfo> clients = new ArrayList<>();


    {
      ClientListInfo user = new ClientListInfo();
      user.id = 123L;
      user.fullName = "Александр Сергеевич Пушкин";
      user.charm = "Горячий";
      user.age = "28";
      user.totalAccountBalance = 10999;
      user.maxAccountBalance = 23087;
      user.minAccountBalance = 2330;
      clients.add(user);
    }

    {
      ClientListInfo user = new ClientListInfo();
      user.id = 321L;
      user.fullName = "Михаил Юрьевич Лермонтов";
      user.charm = "Спокойный";
      user.age = "44";
      user.totalAccountBalance = 6098;
      user.maxAccountBalance = 19021;
      user.minAccountBalance = 1;
      clients.add(user);
    }

    return clients;
  }

  @Override
  public Integer getPageNum(Integer size) {
    if (size == null) {
      //TODO: create matching exception?
      throw new NotFound();
    }

    int ret = db.get().personStorage.size() / size;

    if (ret == 0) ret = 1;

    return ret;
  }
}
