package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.db.DbType;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoadClientList implements ConnectionCallback<List<ClientInfo>> {
  private final String filterBy;
  private final String filterInput;
  private final String orderBy;
  private final boolean isDesc;
  private final int page;
  private final int pageSize;

  public LoadClientList(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize) {
    this.filterBy = filterBy;
    this.filterInput = filterInput;
    this.orderBy = orderBy;
    this.isDesc = isDesc;
    this.page = page;
    this.pageSize = pageSize;
  }

  private final StringBuilder sql = new StringBuilder();
  private final List<Object> params = new ArrayList<>();

  @Override
  public List<ClientInfo> doInConnection(Connection connection) throws Exception {

    prepareSql(DbType.detect(connection));

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object param : params) {
          ps.setObject(index++, param);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {
        List<ClientInfo> ret = new ArrayList<>();

        while (rs.next()) {
          ret.add(readRecord(rs));
        }
        return ret;
      }
    }
  }

  private ClientInfo readRecord(ResultSet rs) throws SQLException {
//    ClientInfo ret = new ClientInfo();
//    ret.id = rs.getString("Client.id");
//    ret.surname = rs.getString("Client.surname");
//    ret.name = rs.getString("Client.name");
//    ret.patronymic = rs.getString("Client.patronymic");
//    ret.charm = new Charm();
//    ret.charm.id = rs.getString("Client.charm");
//    ret.charm.name = rs.getString("Charm.name");
//    ret.charm.description = rs.getString("Charm.description");
//    ret.charm.energy = rs.getDouble("Charm.energy");
////    ret.age = rs.getInt("age");
//    ret.totalBalance = rs.getInt("totalBalance");
//    ret.minBalance = rs.getInt("minBalance");
//    ret.maxBalance = rs.getInt("maxBalance");
//    return ret;

    ClientInfo ret = new ClientInfo();
    ret.id = rs.getString("id");
    ret.surname = rs.getString("surname");
    ret.name = rs.getString("name");
    ret.patronymic = rs.getString("patronymic");
    ret.charm = new Charm();
    ret.charm.id = rs.getString("charm");
    ret.charm.name = rs.getString("cn");
    ret.charm.description = rs.getString("cd");
    ret.charm.energy = rs.getDouble("ce");
    ret.age = rs.getInt("age");
    ret.totalBalance = rs.getDouble("totalBalance");
    ret.minBalance = rs.getDouble("minBalance");
    ret.maxBalance = rs.getDouble("maxBalance");
    return ret;
  }

  private void select() {
    sql.append("select Client.id, Client.surname, Client.name, Client.patronymic, Client.charm, " +
      "Charm.name as cn, Charm.description as cd, Charm.energy as ce, " +
//      "Charm.name, Charm.description, Charm.energy, " +
      "date_part('year', age(Client.birth_date)) as age, " +
      "sum(ClientAccount.money) as totalBalance, " +
      "min(ClientAccount.money) as minBalance, " +
      "max(ClientAccount.money) as maxBalance ");
  }

  private void prepareSql(DbType dbType) {
    select();

    switch (dbType) {

      case Postgres:
        prepareFromWhereForPostgres();
        return;

      case Oracle:
        prepareFromWhereForOracle();
        return;

      default:
        throw new RuntimeException("Unknown DB " + dbType);
    }


  }

  private void prepareFromWhereForOracle() {

  }

  private void prepareFromWhereForPostgres() {
    sql.append("from Client left join Charm on " +
      "Client.charm = Charm.id left join ClientAccount on " +
      "Client.id = ClientAccount.client ");
//    sql.append("from Client inner join Charm on " +
//      "Client.charm = Charm.id ");

    switch (filterBy) {

      case "surname":
        sql.append("where Client.surname like ? ");
        params.add("%" + filterInput + "%");
        return;

      case "name":
        sql.append("where Client.name like ? ");
        params.add("%" + filterInput + "%");
        return;

      case "patronymic":
        sql.append("where Client.patronymic like ? ");
        params.add("%" + filterInput + "%");
        return;

    }
    sql.append("group by client.id, charm.id ");

    switch (orderBy) {
      case "age":
        sql.append("order by age ");
        return;

      case "totalBalance":
        sql.append("order by totalBalance ");
        return;

      case "minBalance":
        sql.append("order by minBalance ");
        return;

      case "maxBalance":
        sql.append("order by maxBalance ");
        return;

      default:
        sql.append("order by Client.surname ");
    }

    if (isDesc) sql.append("desc ");

    sql.append("limit ? offset ? ");
    params.add(pageSize);
    params.add(page * pageSize);
  }
}
