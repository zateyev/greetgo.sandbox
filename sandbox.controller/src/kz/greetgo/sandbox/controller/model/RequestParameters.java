package kz.greetgo.sandbox.controller.model;


public class RequestParameters {
  public String filterBy;
  public String filterInput;
  public String orderBy;
  public boolean isDesc;
  public int page;
  public int pageSize;

  public RequestParameters() {
    this.filterBy = "";
    this.filterInput = "";
    this.orderBy = "";
    this.isDesc = false;
    this.page = 0;
    this.pageSize = 0;
  }

  public RequestParameters(int page, int pageSize) {
    this.page = page;
    this.pageSize = pageSize;
  }
}
