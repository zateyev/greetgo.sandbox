package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.ColumnSortType;

public class ClientRecordListRequest {
  public long clientRecordCountToSkip;
  public long clientRecordCount;
  public ColumnSortType columnSortType;
  public boolean sortAscend;
}
