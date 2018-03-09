package kz.greetgo.sandbox.db.migration_impl.report;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;

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
    sheet.setWidth(2, 30.71);
    sheet.setWidth(3, 13.86);

    sheet.skipRow();

    sheet.row().start();
    sheet.style().font().bold();
    sheet.cellStr(1, "Имя файла");
    sheet.cellStr(2, "Количество записей");
    sheet.cellStr(3, "Время загрузки");
    sheet.style().clean();
    sheet.row().finish();
  }

  public void addRow(String fileName, int recordsCount, String time) {
    sheet.row().start();
    if (fileName == null) fileName = "";
    if (time == null) time = "";
    sheet.cellStr(1, fileName);
    sheet.cellStr(2, String.valueOf(recordsCount));
    sheet.cellStr(3, time);
    sheet.row().finish();
  }

  public void finish() {
    xlsx.complete(out);
  }
}
