package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;

public interface ClientRegister {
  ClientRecord[] getList(int page);
  ClientDetails getClient(String id);
  ClientRecord saveClient(String id, String json);
  void deleteClient(String id);
}
