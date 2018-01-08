package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.report.ClientRecord.ClientReportView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportTestView implements ClientReportView {

  public final List<ClientRecord> row = new ArrayList<>();

  @Override
  public void start() {}

  @Override
  public void append(ClientRecord row) {
    this.row.add(row);
  }

  @Override
  public void finish(String fio) {

  }
}
