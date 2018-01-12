package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetClientListSize extends AbstractGetClientLogic implements ConnectionCallback<Long> {

  public GetClientListSize(ClientListRequest in) {
    super(in);
  }

  @Override
  protected void appendJoin() {
    sql.append(" join client_account ac on c.id = ac.client");
  }

  @Override
  protected void appendGroupBy() {}

  @Override
  protected void appendOffsetLimit() {}

  @Override
  protected void appendSorting() {}

  @Override
  protected void appendSelect() {
    sql.append("select count(distinct(c.id))");
  }

  @Override
  public Long doInConnection(Connection connection) throws Exception {
    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object param : sqlParams) {
          ps.setObject(index++, param);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {

        if (!rs.next()) throw new RuntimeException("Result Set Incorrect");

        return rs.getLong(1);

      }

    }
  }
}
