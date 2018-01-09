package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.sandbox.controller.model.ClientListRequest;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractGetClientList extends AbstractGetClientLogic {

  public AbstractGetClientList(ClientListRequest in) {
    super(in);
  }

  private static String makeFio(String surname, String name, String patronymic) {
    return surname + ' ' + name + (patronymic == null ? "" : " " + patronymic);
  }

  protected ClientRecord rsToClient(ResultSet rs) throws SQLException {
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
  protected abstract void appendOffsetLimit();

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
