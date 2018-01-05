package kz.greetgo.sandbox.db.migration;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class InsertClients implements ConnectionCallback<Void>{

  private final String sql = "insert into client (id, name, surname, patronymic, birth_date, current_gender, charm_id, actual) " +
    "values (?,?,?,?,?,?,?,1); ";

  private final List<Object> sqlParams = new ArrayList<>();
  private SAXParsClient parsClient;

  public InsertClients(SAXParsClient parsClient) {

    this.parsClient = parsClient;
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    try(PreparedStatement ps = connection.prepareStatement(sql)){



    }
    return null;
  }
}
