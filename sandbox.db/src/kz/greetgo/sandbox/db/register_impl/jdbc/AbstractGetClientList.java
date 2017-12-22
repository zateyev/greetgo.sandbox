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
    sql.append(" from client c where 1=1");

    if (in.filterByFio != null && in.filterByFio.length() > 0) {
      sql.append(" and ((surname like '?%') or ( like '?%') or ( like '?%'))");
      sqlParams.add(in.filterByFio);
      sqlParams.add(in.filterByFio);
      sqlParams.add(in.filterByFio);
    }

    appendSorting();

    appendOffsetLimit();
  }

  protected void appendSorting() {
    if (in.sort == null) return;
    switch (in.sort) {
      case "asd":
        sql.append(" order by asd");
        return;
      case "dsa":
        sql.append(" order by dsa");
        return;
    }

  }

  protected abstract void appendOffsetLimit();

  protected abstract void select();
}
