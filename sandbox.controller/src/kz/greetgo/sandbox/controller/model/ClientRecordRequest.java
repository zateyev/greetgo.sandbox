package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.ColumnSortType;

public class ClientRecordRequest {
  public long clientRecordCountToSkip;
  public long clientRecordCount;
  public ColumnSortType columnSortType;
  public boolean sortAscend;
  public String nameFilter;
}
