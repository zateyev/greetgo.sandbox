package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientRecordListReportViewPdf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetPdfReport extends AbstractGetClientList implements ConnectionCallback<Void> {
  private ClientRecordListReportViewPdf pdf;

  public GetPdfReport(ClientRecordListReportViewPdf pdf, ClientListRequest in) {
    super(in);

    this.pdf = pdf;
  }


  @Override
  public Void doInConnection(Connection connection) throws Exception {
    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      try (ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

          pdf.append(rsToClient(rs));

        }

      }
      return null;
    }
  }
}
