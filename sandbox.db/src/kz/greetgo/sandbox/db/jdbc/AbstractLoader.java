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

  protected void prepareSql(DbType dbType, boolean isOrdered, boolean isListLimited) {
    select();

    switch (dbType) {

      case Postgres:
        prepareFromWhereForPostgres(isOrdered, isListLimited);
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

  protected void prepareFromWhereForPostgres(boolean isOrdered, boolean isListLimited) {

    sql.append("from Client left join " +
      "(select client, sum(money) totalBalance, min(money) minBalance, " +
      "max(money) maxBalance from ClientAccount group by client) ca on ca.client = Client.id " +
      "left join Charm on Client.charm = Charm.id ");

    switch (filterBy) {

      case "surname":
        sql.append("where lower(Client.surname) like lower(?) ");
        params.add("%" + filterInput + "%");
        break;

      case "name":
        sql.append("where lower(Client.name) like lower(?) ");
        params.add("%" + filterInput + "%");
        break;

      case "patronymic":
        sql.append("where lower(Client.patronymic) like lower(?) ");
        params.add("%" + filterInput + "%");
        break;

    }

    if (isOrdered) {
      switch (orderBy) {
        case "age":
          sql.append("order by age ");
          break;

        case "totalBalance":
          sql.append("order by ca.totalBalance nulls first, lower(Client.surname) ");
          break;

        case "minBalance":
          sql.append("order by ca.minBalance ");
          break;

        case "maxBalance":
          sql.append("order by ca.maxBalance ");
          break;

        default:
          sql.append("order by lower(Client.surname) ");
      }

      if (isDesc) sql.append("desc ");
    }

    if (isListLimited) {
      sql.append("limit ? offset ? ");
      params.add(pageSize);
      params.add(page * pageSize);
    }
  }
}
