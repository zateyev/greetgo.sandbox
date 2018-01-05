package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientRecordListReportViewPdf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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


  private static String makeFio(String surname, String name, String patronymic) {
    return surname + ' ' + name + (patronymic == null ? "" : " " + patronymic);
  }

  // FIXME: 1/5/18 Попробуй избавиться от повторения кода
  private ClientRecord rsToClient(ResultSet rs) throws SQLException {
    ClientRecord ret = new ClientRecord();
    ret.id = rs.getString("id");
    ret.fio = makeFio(rs.getString("surname"), rs.getString("name"), rs.getString("patronymic"));
    ret.age = rs.getInt("age");
    ret.charm = rs.getString("charm");
    ret.totalAccountBalance = rs.getFloat("total");
    ret.maxAccountBalance = rs.getFloat("max");
    ret.minAccountBalance = rs.getFloat("min");
    return ret;
  }

  @Override
  protected void appendJoin() {
    sql.append(" join charm ch on c.charm_id = ch.id " +
      " join client_account c_ac on c_ac.client = c.id");

  }

  @Override
  protected void appendGroupBy() {
    sql.append(" group by c.id, charm");
  }

  @Override
  protected void appendOffsetLimit() {
    if (in.count > 0) {
      sql.append(" limit ? offset ?");
      sqlParams.add(in.count);
      sqlParams.add(in.skipFirst);
    }
  }

  @Override
  protected void appendSelect() {
    sql.append("select c.id, c.surname, c.name, c.patronymic, " +
      " ch.name as charm," +
      " extract(year from age(c.birth_date)) as age," +
      " sum(c_ac.money) as total," +
      " max(c_ac.money) as max," +
      " min(c_ac.money) as min");
  }


}
