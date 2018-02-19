package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.db.DbType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoader<T> implements ConnectionCallback<T> {
  private final String filterBy;
  private final String filterInput;
  private final String orderBy;
  private final boolean isDesc;
  private final int page;
  private final int pageSize;

  protected AbstractLoader(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize) {
    this.filterBy = filterBy;
    this.filterInput = filterInput;
    this.orderBy = orderBy;
    this.isDesc = isDesc;
    this.page = page;
    this.pageSize = pageSize;
  }

  protected final StringBuilder sql = new StringBuilder();
  protected final List<Object> params = new ArrayList<>();

  abstract void select();

  protected void prepareSql(DbType dbType) {
    select();

    switch (dbType) {

      case Postgres:
        prepareFromWhereForPostgres();
        return;

      case Oracle:
        prepareFromWhereForOracle();
        return;

      default:
        throw new RuntimeException("Unknown DB " + dbType);
    }

  }

  private void prepareFromWhereForOracle() {

  }

  protected void prepareFromWhereForPostgres() {

    sql.append("from Client left join " +
      "(select client, sum(money) totalBalance, min(money) minBalance, " +
      "max(money) maxBalance from ClientAccount group by client) ca on ca.client = Client.id " +
      "left join Charm on Client.charm = Charm.id ");

    switch (filterBy) {

      case "surname":
        sql.append("where Client.surname like ? ");
        params.add("%" + filterInput + "%");
        return;

      case "name":
        sql.append("where Client.name like ? ");
        params.add("%" + filterInput + "%");
        return;

      case "patronymic":
        sql.append("where Client.patronymic like ? ");
        params.add("%" + filterInput + "%");
        return;

    }

    if (pageSize > 0) {

      switch (orderBy) {
        case "age":
          sql.append("order by age ");
          return;

        case "totalBalance":
          sql.append("order by totalBalance ");
          return;

        case "minBalance":
          sql.append("order by minBalance ");
          return;

        case "maxBalance":
          sql.append("order by maxBalance ");
          return;

        default:
          sql.append("order by Client.surname ");
      }

      if (isDesc) sql.append("desc ");

      sql.append("limit ? offset ? ");
      params.add(pageSize);
      params.add(page * pageSize);
    }
  }
}
