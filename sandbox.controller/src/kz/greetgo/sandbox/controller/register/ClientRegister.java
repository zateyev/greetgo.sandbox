package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.io.OutputStream;
import java.util.List;

public interface ClientRegister {

  long getSize(ClientListRequest clientListRequest);

  List<ClientRecord> getList(ClientListRequest clientListRequest);

  ClientDetails getClient(String id);

  ClientRecord saveClient(ClientToSave clientToSave);

  void deleteClient(String id);

  void getClientListForReport(ClientListRequest clientListRequest,
                              OutputStream outputStream,
                              String contentType,
                              String personId) throws Exception;
}
