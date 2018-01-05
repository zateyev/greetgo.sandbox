package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientRecordListReportViewXslx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetXlsxReport extends AbstractGetClientList implements ConnectionCallback<Void> {
  private ClientRecordListReportViewXslx view;

  public GetXlsxReport(ClientRecordListReportViewXslx view, ClientListRequest in) {
    super(in);
    this.view = view;
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    prepareSql();
    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
      try (ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

          view.append(rsToClient(rs));

        }

      }
      return null;
    }
  }

}
