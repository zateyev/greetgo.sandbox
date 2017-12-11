package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.List;

public interface ClientRegister {
  long getSize();

  List<ClientRecord> getList(int page, String sort);

  ClientDetails getClient(String id);

  ClientRecord saveClient(ClientToSave clientToSave);

  void deleteClient(String id);
}
