package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ClientRegister {

  long getSize(ClientListRequest clientListRequest);

  List<ClientRecord> getList(ClientListRequest clientListRequest);

  ClientDetails getClient(String id);

  ClientRecord saveClient(ClientToSave clientToSave);

  void deleteClient(String id);

  void getFile(ClientListRequest clientListRequest);
}
