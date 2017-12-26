package kz.greetgo.sandbox.db.register_impl.jdbc.reports;

import kz.greetgo.sandbox.controller.model.ClientRecord;

public interface ClientReportView {
  void begin();

  void addRecord(ClientRecord clientRecord);

  void finish(String fio);
}
