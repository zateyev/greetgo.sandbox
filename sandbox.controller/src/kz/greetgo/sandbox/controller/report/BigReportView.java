package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientInfo;

public interface BigReportView {

  void start(ReportHeadData headData);

  void addRow(ClientInfo row);

  void finish(ReportFootData footData);

}
