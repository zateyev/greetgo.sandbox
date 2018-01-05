package kz.greetgo.sandbox.db.migration;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.sql.Connection;
import java.util.List;


public class InsertClients implements ConnectionCallback<Void>{
  public InsertClients(List<ClientToSave> clientList) {}

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    return null;
  }
}
