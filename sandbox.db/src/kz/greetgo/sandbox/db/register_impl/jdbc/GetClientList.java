package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetClientList extends AbstractGetClientList implements ConnectionCallback<List<ClientRecord>> {

  public GetClientList(ClientListRequest in) {
    super(in);
  }

  @Override
  protected void appendOffsetLimit() {
    if (in.count > 0) {
      sql.append(" offset ? limit ?");
      sqlParams.add(in.skipFirst);
      sqlParams.add(in.count);
    }
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {

    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
      System.out.println(sql.toString());

      {
        int index = 1;
        for (Object param : sqlParams) {
          ps.setObject(index++, param);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {

        List<ClientRecord> ret = new ArrayList<>();

        while (rs.next()) {

          ret.add(readRecord(rs));

        }

        return ret;

      }

    }

  }

  @Override
  protected void select() {
    sql.append("select c.id, c.surname, c.name, c.patronymic, " +
      " ch.name as charm," +
      " extract(year from age(c.birth_date)) as age");
  }


  private ClientRecord readRecord(ResultSet rs) throws SQLException {
    ClientRecord ret = new ClientRecord();
    ret.id = rs.getString("id");
    ret.fio = makeFio(rs.getString("surname"), rs.getString("name"), rs.getString("patronymic"));
    ret.age = rs.getInt("age");
    ret.charm = rs.getString("charm");
    return ret;
  }

  private static String makeFio(String surname, String name, String patronymic) {
    return surname + ' ' + name + (patronymic == null ? "" : " " + patronymic);
  }


}
