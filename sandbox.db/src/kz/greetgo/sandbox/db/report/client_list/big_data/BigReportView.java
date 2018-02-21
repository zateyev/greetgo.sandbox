package kz.greetgo.sandbox.db.report.client_list.big_data;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.db.report.client_list.ReportFootData;
import kz.greetgo.sandbox.db.report.client_list.ReportHeadData;

public interface BigReportView {

  void start(ReportHeadData headData) throws DocumentException;

  void addRow(ClientInfo row);

  void finish(ReportFootData footData) throws DocumentException;

}
