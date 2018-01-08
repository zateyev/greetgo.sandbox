package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientPhones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetClientPhones implements ConnectionCallback<ClientPhones> {

  private final String sql = "select number, type from client_phone where client = ? and actual = 1";

  private final List<Object> sqlParams = new ArrayList<>();

  public GetClientPhones(String id) {
    sqlParams.add(id);
  }

  @Override
  public ClientPhones doInConnection(Connection connection) throws Exception {

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object p : sqlParams) {
          ps.setObject(index++, p);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {
        ClientPhones phones = new ClientPhones();
        while (rs.next()) {
          readPhones(rs, phones);
        }
        return phones;
      }
    }
  }

  private void readPhones(ResultSet rs, ClientPhones phones) throws SQLException {
    switch (rs.getString("type")) {
      case "home":
        phones.home = rs.getString("number");
        return;
      case "work":
        phones.work = rs.getString("number");
        return;
      case "mobile":
        phones.mobile.add(rs.getString("number"));
        return;
    }
  }
}
