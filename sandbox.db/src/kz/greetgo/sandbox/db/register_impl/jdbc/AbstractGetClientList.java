package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.sandbox.controller.model.ClientListRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGetClientList {
  protected final ClientListRequest in;

  public AbstractGetClientList(ClientListRequest in) {this.in = in;}

  protected final StringBuilder sql = new StringBuilder();
  protected final List<Object> sqlParams = new ArrayList<>();

  protected void prepareSql() {
    select();
    //Khamit show only client having at least obe active account - 1

    sql.append(" from client c join charm ch on c.charm_id = ch.id " +
      " join client_account c_ac on c_ac.client = c.id" +
      " where 1=1");


    //Khamit string.isEmpty(). esli fio="  " ili fio=" pushkin" - 1
    if (in.filterByFio != null && in.filterByFio.length() > 0) {
      sql.append(" and ( (c.surname like ?||'%') or ( c.name like ?||'%') or ( c.patronymic like ?||'%') )");
      sqlParams.add(in.filterByFio);
      sqlParams.add(in.filterByFio);
      sqlParams.add(in.filterByFio);
    }

    sql.append(" and c.actual = 1");

    appendGroupBy();

    appendSorting();

    appendOffsetLimit();

  }

  protected abstract void appendGroupBy();

  protected void appendSorting() {
    if (in.sort == null) return;
    switch (in.sort) {
      case "age":
        sql.append(" order by age");
        return;
      case "ageDesc":
        sql.append(" order by age desc");
        return;
      case "total":
        sql.append(" order by total");
        return;
      case "totalDesc":
        sql.append(" order by total desc");
        return;
      case "max":
        sql.append(" order by max");
        return;
      case "maxDesc":
        sql.append(" order by max desc");
        return;
      case "min":
        sql.append(" order by min");
        return;
      case "minDesc":
        sql.append(" order by min desc");
        return;

        //Khamit return or throw exception - 1
      default:
        return;
    }

  }

  protected abstract void appendOffsetLimit();

  protected abstract void select();
}
