package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.DbType;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.db.report.client_list.big_data.ReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class LoadClientListToReport extends LoadClientList {

  private ReportView reportView;

  public LoadClientListToReport(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize, ReportView view) {
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
        }
        return null;
      }
    }
  }

  @Override
  void prepareSql(DbType dbType) {
    select();

    switch (dbType) {

      case Postgres:
        from();
        where();
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
