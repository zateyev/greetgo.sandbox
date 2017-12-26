package kz.greetgo.sandbox.db.register_impl.jdbc.reports;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.OutputStream;

public class ClientReportViewPdf implements ClientReportView {
  private final OutputStream outputStream;

  @Override
  public void begin() {

  }

  @Override
  public void addRecord(ClientRecord clientRecord) {

  }

  @Override
  public void finish(String fio) {

  }

  public ClientReportViewPdf(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public static void main(String[] args) {
    System.out.println("test it");
  }
}
