package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.sandbox.controller.model.ClientRecordRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class GetClientCommon {

  protected final ClientRecordRequest request;

  protected final StringBuilder sqlQuery = new StringBuilder();
  protected final List<Object> sqlParamList = new ArrayList<>();

  protected GetClientCommon(ClientRecordRequest request) {
    this.request = request;
  }

  protected void prepareSql() {
    select();
    sqlQuery.append("FROM client AS cl ");
    from();
    sqlQuery.append("WHERE cl.actual=1 ");

    if (!request.nameFilter.isEmpty()) {
      // TODO: make lower case in java?
      sqlQuery.append("AND (LOWER(cl.surname) LIKE LOWER(?) OR " +
        "LOWER(cl.name) LIKE LOWER(?) OR " +
        "LOWER(cl.patronymic) LIKE LOWER(?)) ");
      sqlParamList.add("%" + request.nameFilter + "%");
      sqlParamList.add("%" + request.nameFilter + "%");
      sqlParamList.add("%" + request.nameFilter + "%");
    }

    group();
    sort();
    limit();
  }

  protected abstract void select();

  protected abstract void from();

  protected abstract void group();

  protected abstract void sort();

  protected abstract void limit();
}
