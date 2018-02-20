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

  AbstractLoader(String filterBy, String filterInput, String orderBy, boolean isDesc, int page, int pageSize) {
    this.filterBy = filterBy;
    this.filterInput = filterInput;
    this.orderBy = orderBy;
    this.isDesc = isDesc;
    this.page = page;
    this.pageSize = pageSize;
  }

  protected final StringBuilder sql = new StringBuilder();
  final List<Object> params = new ArrayList<>();

  abstract void select();

  abstract void prepareSql(DbType dbType);

  void prepareFromWhereForPostgres() {
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
  }

  void orderBy() {
    sql.append("order by ");
    switch (orderBy) {
      case "age":
        if (isDesc)
          sql.append("age desc nulls last, lower(Client.surname) ");
        else
          sql.append("age nulls first, lower(Client.surname) ");
        break;

      case "totalBalance":
        if (isDesc)
          sql.append("ca.totalBalance desc nulls last, lower(Client.surname) ");
        else
          sql.append("ca.totalBalance nulls first, lower(Client.surname) ");
        break;

      case "minBalance":
        if (isDesc)
          sql.append("ca.minBalance desc nulls last, lower(Client.surname) ");
        else
          sql.append("ca.minBalance nulls first, lower(Client.surname) ");
        break;

      case "maxBalance":
        if (isDesc)
          sql.append("ca.maxBalance desc nulls last, lower(Client.surname) ");
        else
          sql.append("ca.maxBalance nulls first, lower(Client.surname) ");
        break;

      default:
        sql.append("lower(Client.surname) ");
    }
  }

  void limit() {
    sql.append("limit ? offset ? ");
    params.add(pageSize);
    params.add(page * pageSize);
  }

  void prepareFromWhereForOracle() {

  }
}
