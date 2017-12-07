package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;



@Bean
public class ClientRegisterImpl implements ClientRegister {

  @Override
  public ClientRecord[] getList(int page) {
    return new ClientRecord[0];
  }

  @Override
  public ClientDetails getClient(String id) {
    return null;
  }

  @Override
  public ClientRecord saveClient(String id, String json) {
    return null;
  }

  @Override
  public void deleteClient(String id) {

  }
}
