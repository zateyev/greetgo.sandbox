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

    appendSelect();

    sql.append(" from client c");

    appendJoin();

    sql.append(" where 1=1");

    appendFilter();

    sql.append(" and c.actual = 1");

    appendGroupBy();

    appendSorting();

    appendOffsetLimit();

  }

  protected abstract void appendJoin();

  protected abstract void appendGroupBy();

  protected abstract void appendOffsetLimit();

  protected abstract void appendSelect();


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
      default:
        throw new RuntimeException("Undeclared column name for sort: " + in.sort);
    }

  }

  protected void appendFilter() {
    if (in.filterByFio != null && !in.filterByFio.isEmpty()) {
      String s = in.filterByFio.trim();
      sql.append(" and ( (c.surname like ?||'%') or ( c.name like ?||'%') or ( c.patronymic like ?||'%') )");
      sqlParams.add(s);
      sqlParams.add(s);
      sqlParams.add(s);
    }
  }

}
