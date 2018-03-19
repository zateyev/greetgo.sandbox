package kz.greetgo.sandbox.db.migration_impl.report;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;

import java.io.IOException;
import java.io.OutputStream;

public class ReportXlsx {
  private final Xlsx xlsx = new Xlsx();
  private OutputStream out;
  private Sheet sheet;

  public ReportXlsx(OutputStream out) {
    this.out = out;
  }

  public void start() {
    sheet = xlsx.newSheet(true);

    sheet.setWidth(1, 30.71);
    sheet.setWidth(2, 60.71);

    sheet.skipRow();

    sheet.row().start();
    sheet.style().font().bold();
    sheet.cellStr(1, "Время запроса");
    sheet.cellStr(2, "Запрос");
    sheet.style().clean();
    sheet.row().finish();
  }

  public void addRow(String executingSql, String time_str) {
    sheet.row().start();
    double time = 0;
    if (executingSql == null) executingSql = "";
    if (time_str != null) time = Double.valueOf(time_str);
    sheet.cellDouble(1, time);
    sheet.cellStr(2, executingSql);
    sheet.row().finish();
  }

  public void finish() throws IOException {
    xlsx.complete(out);
    out.flush();
    out.close();
  }
}
