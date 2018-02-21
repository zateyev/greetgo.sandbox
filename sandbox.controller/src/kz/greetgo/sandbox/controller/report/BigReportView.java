package kz.greetgo.sandbox.controller.report;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientInfo;

public interface BigReportView {

  void start(ReportHeadData headData) throws DocumentException;

  void addRow(ClientInfo row);

  void finish(ReportFootData footData) throws DocumentException;

}
