package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.DbType;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.db.report.client_list.big_data.BigReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BigReportJdbc extends AbstractLoader<List<ClientInfo>> {

  private BigReportView reportView;

  public BigReportJdbc(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize, BigReportView view) {
    super(filterBy, filterInput, orderBy, isDesc, page, pageSize);
    this.reportView = view;
  }

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

        while (rs.next()) {

          reportView.addRow(readRecord(rs));
//          ret.add(readRecord(rs));
        }
        return null;
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
    ret.charm.name = rs.getString("cn");
    ret.charm.description = rs.getString("cd");
    ret.charm.energy = rs.getDouble("ce");
    ret.age = rs.getInt("age");
    ret.totalBalance = rs.getDouble("totalBalance");
    ret.minBalance = rs.getDouble("minBalance");
    ret.maxBalance = rs.getDouble("maxBalance");
    return ret;
  }

  @Override
  protected void select() {
    sql.append("select Client.id, Client.surname, Client.name, Client.patronymic, Client.charm, " +
      "Charm.name as cn, Charm.description as cd, Charm.energy as ce, " +
      "date_part('year', age(Client.birth_date)) as age, " +
      "ca.totalBalance, " +
      "ca.minBalance, " +
      "ca.maxBalance ");
  }

  @Override
  void prepareSql(DbType dbType) {
    select();

    switch (dbType) {

      case Postgres:
        prepareFromWhereForPostgres();
        orderBy();
        return;

      case Oracle:
        prepareFromWhereForOracle();
        return;

      default:
        throw new RuntimeException("Unknown DB " + dbType);
    }
  }
}
