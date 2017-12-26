package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
  @Override
  public List<ClientRecord> list() {
    List<ClientRecord> ret = new ArrayList<>();
    ret.add(record("a10", "Иванов Иван"));
    ret.add(record("a11", "Петров Иван"));
    ret.add(record("a12", "Сидоров Иван"));
    ret.add(record("a13", "Абрамов Иван"));
    return ret;
  }

  private ClientRecord record(String id, String fio) {
    ClientRecord ret = new ClientRecord();
    ret.id = id;
    ret.fio = fio;
    return ret;
  }
}
