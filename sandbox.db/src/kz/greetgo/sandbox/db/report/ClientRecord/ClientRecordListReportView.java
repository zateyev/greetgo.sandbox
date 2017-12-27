package kz.greetgo.sandbox.db.report.ClientRecord;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.Date;

public interface ClientRecordListReportView {
  void start(Date onDate);

  void append(ClientRecord row);

  void finish();
}
