package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;

public interface ClientRegister {
  ClientRecord[] getList(int page);
  ClientDetails getClient(String id);
  ClientRecord saveClient(ClientToSave clientToSave);
  void deleteClient(String id);
}
