package kz.greetgo.sandbox.db.report.ClientRecord;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.IOException;

public interface ClientReportView {
  void start() throws DocumentException, IOException;

  void append(ClientRecord row) throws DocumentException;

  void finish(String fio) throws IOException, DocumentException;
}
