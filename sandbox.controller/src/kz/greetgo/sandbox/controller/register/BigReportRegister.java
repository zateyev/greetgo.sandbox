package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.report.BigReportView;

import java.io.PrintStream;

public interface BigReportRegister {
  void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, BigReportView view);
}
