package kz.greetgo.sandbox.db.report.ClientRecord;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.util.RND;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientReportViewPdfProbe {
  public static void main(String[] args) throws IOException, DocumentException {
    OutputStream file = new FileOutputStream("ViewTest.pdf");

    List<ClientRecord> list = new ArrayList<>();

    for (int i = 0; i < 50; i++) {
      ClientRecord rec = new ClientRecord();
      rec.fio = RND.str(10) + " " + RND.str(10) + " " + RND.str(10);
      rec.charm = RND.str(7);
      rec.age = RND.plusInt(100);
      rec.totalAccountBalance = (long) RND.plusInt(999999);
      rec.maxAccountBalance = (long) RND.plusInt(999999);
      rec.minAccountBalance = (long) RND.plusInt(999999);

      list.add(rec);
    }


    ClientReportViewPdf pdf = new ClientReportViewPdf(file);
    //TODO
  }
}
