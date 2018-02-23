package kz.greetgo.sandbox.db.report.client_list;

import kz.greetgo.sandbox.controller.model.ClientInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ReportViewXlsx implements ReportView {

  private OutputStream outputStream;

  public ReportViewXlsx(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void generate(ClientInfo clientInfo) throws Exception {
    // here pdf must be generated

    StringBuilder sb = new StringBuilder();
    sb.append("some xlsx markup");
    sb.append("id: ").append(clientInfo.id);
    sb.append("surname: ").append(clientInfo.surname);

    outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  public static void main(String[] args) throws Exception {

    File file = new File("build/report/result.xlsx");
    file.getParentFile().mkdirs();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      ReportViewXlsx reportViewPdf = new ReportViewXlsx(outputStream);

      ClientInfo in = new ClientInfo();
      in.id = "kz001";
      in.surname = "Asdov";
      reportViewPdf.generate(in);
    }
  }
}
