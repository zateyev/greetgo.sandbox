package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;

import java.util.ArrayList;
import java.util.List;

@Bean
public class CharmRegisterStand implements CharmRegister {
  public BeanGetter<ClientStandDb> clientD;

  @Override
  public List<Charm> getCharms() {
    return new ArrayList<>(clientD.get().charmStorage.values());
  }
}
