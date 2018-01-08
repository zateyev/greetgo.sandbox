package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetClientList extends AbstractGetClientList implements ConnectionCallback<List<ClientRecord>> {

  public GetClientList(ClientListRequest in) {
    super(in);
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {

    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object param : sqlParams) {
          ps.setObject(index++, param);
        }
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<ClientRecord> ret = new ArrayList<>();
        while (rs.next()) {
          ret.add(rsToClient(rs));
        }
        return ret;
      }
    }

  }

}
