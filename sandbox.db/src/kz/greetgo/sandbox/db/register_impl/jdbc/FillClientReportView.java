package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.db.register_impl.jdbc.reports.ClientReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FillClientReportView extends AbstractGetClientList implements ConnectionCallback<Void> {
  private final String personId;
  private final ClientReportView view;

  public FillClientReportView(ClientListRequest clientListRequest, String personId, ClientReportView view) {
    super(clientListRequest);
    this.personId = personId;
    this.view = view;
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {

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

        view.begin();

        while (rs.next()) {
          view.addRecord(readRecord(rs));
        }

        view.finish("Ивано");
        return null;
      }

    }

  }

}