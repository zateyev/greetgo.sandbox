package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.report.ViewType;

import java.io.OutputStream;
import java.io.PrintStream;

public interface BigReportRegister {
  void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, ViewType viewType, OutputStream out) throws Exception;
}
