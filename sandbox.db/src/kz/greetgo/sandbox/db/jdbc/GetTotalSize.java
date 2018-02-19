package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.DbType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetTotalSize extends AbstractLoader<Long> {

  public GetTotalSize(String filterBy, String filterInput) {
    super(filterBy, filterInput, "", false, 0, 0);
  }


  @Override
  public Long doInConnection(Connection connection) throws Exception {
    prepareSql(DbType.detect(connection), false, false);

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object param : params) {
          ps.setObject(index++, param);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {
        Long ret = 0L;

        if (rs.next()) {
          ret = rs.getLong("size");
        }
        return ret;
      }
    }
  }

  @Override
  protected void select() {
    sql.append("select count(client.id) as size ");
  }
}
