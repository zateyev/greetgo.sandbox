package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.ListInfo;

import java.util.List;

public interface ClientRegister {
  long getSize(ListInfo listInfo);

  List<ClientRecord> getList(ListInfo listInfo);

  ClientDetails getClient(String id);

  ClientRecord saveClient(ClientToSave clientToSave);

  void deleteClient(String id);
}
