package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetClientCount extends GetClientCommon implements ConnectionCallback<Long> {
  public GetClientCount(ClientRecordRequest request) {
    super(request);
  }

  @Override
  public Long doInConnection(Connection connection) throws Exception {
    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
      int index = 1;
      for (Object sqlParam : sqlParamList)
        ps.setObject(index++, sqlParam);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) throw new RuntimeException("No rows of result set");
        return rs.getLong(1);
      }
    }
  }

  @Override
  protected void select() {
    sqlQuery.append("SELECT COUNT(*) ");
  }

  @Override
  protected void from() {

  }

  @Override
  protected void group() {

  }

  @Override
  protected void sort() {

  }

  @Override
  protected void limit() {

  }
}
