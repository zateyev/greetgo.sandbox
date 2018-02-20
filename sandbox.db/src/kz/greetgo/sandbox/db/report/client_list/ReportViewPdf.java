package kz.greetgo.sandbox.db.report.client_list;

import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.report.ReportView;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ReportViewPdf implements ReportView {

  private OutputStream outputStream;

  public ReportViewPdf(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void generate(ClientInfo clientInfo) throws Exception {
    // here pdf must be generated

    StringBuilder sb = new StringBuilder();
    sb.append("some pdf markup");
    sb.append("id: ").append(clientInfo.id);
    sb.append("surname: ").append(clientInfo.surname);

    outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  public static void main(String[] args) throws Exception {

    File file = new File("build/report/result.txt");
    file.getParentFile().mkdirs();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      ReportViewPdf reportViewPdf = new ReportViewPdf(outputStream);

      ClientInfo in = new ClientInfo();
      in.id = "kz001";
      in.surname = "Asdov";
      reportViewPdf.generate(in);
    }
  }
}
