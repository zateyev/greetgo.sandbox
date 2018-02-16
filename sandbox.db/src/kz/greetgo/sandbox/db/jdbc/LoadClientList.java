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
    ClientInfo ret = new ClientInfo();
    ret.id = rs.getString("id");
    ret.surname = rs.getString("surname");
    ret.name = rs.getString("name");
    ret.patronymic = rs.getString("patronymic");
    ret.charm = new Charm();
    ret.charm.id = rs.getString("charm");
    ret.charm.name = rs.getString("Charm.name");
    ret.charm.description = rs.getString("Charm.description");
    ret.charm.energy = rs.getDouble("Charm.energy");
    ret.age = rs.getInt("age");
    ret.totalBalance = rs.getInt("totalBalance");
    ret.minBalance = rs.getInt("minBalance");
    ret.maxBalance = rs.getInt("maxBalance");
    return ret;
  }

  private void select() {
    sql.append("select id, surname, name, patronymic, charm, Charm.name, Charm.description, Charm.energy, " +
      "DIF(now() - birth_date) as age, " +
      "tot() as totalBalance, " +
      "min() as minBalance, " +
      "max() as maxBalance");
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
    sql.append("from Client ");
    sql.append("where ...");

    sql.append("and asd = ?");
    params.add("wow");
  }
}
