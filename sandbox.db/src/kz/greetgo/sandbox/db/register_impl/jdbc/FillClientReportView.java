package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FillClientReportView extends AbstractGetClientList implements ConnectionCallback<Void> {
  private ClientReportView view;
  private String personId;
  private String getNameSql = "select surname, name, patronymic from person where id = ?";
  private static String fio;

  public FillClientReportView(ClientReportView view, ClientListRequest in, String personId) {
    super(in);
    this.view = view;
    this.personId = personId;
  }

  private static void makeFio(String surname,
                         String name,
                         String patronymic){
    fio = surname + ' ' + name + (patronymic == null ? "" : " " + patronymic);
  }

  @Override
  protected void appendOffsetLimit() {}

  @Override
  public Void doInConnection(Connection connection) throws Exception {

    try(PreparedStatement ps = connection.prepareStatement(getNameSql)){

      ps.setObject(1, personId);

      try(ResultSet rs = ps.executeQuery()){
        if (!rs.next()) throw new RuntimeException("Result Set Incorrect");
        makeFio(rs.getString(1), rs.getString(2), rs.getString(3));
      }

    }

    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      {
        int index = 1;
        for (Object p : sqlParams) {
          ps.setObject(index++, p);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {

        view.start();

        while (rs.next()) {

          view.append(rsToClient(rs));

        }

        view.finish(fio);

      }
      return null;
    }
  }
}
