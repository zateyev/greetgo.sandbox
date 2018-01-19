package kz.greetgo.sandbox.db.report.SqlExecutionTime;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;

import java.io.OutputStream;

public class SqlExecutionTimeView {

  private OutputStream out;

  public SqlExecutionTimeView(OutputStream out) {
    this.out = out;
  }

  private final Xlsx xlsx = new Xlsx();
  private Sheet sheet = xlsx.newSheet(true);

  public void append(double sec, String sql) {

    sheet.row().start();
    sheet.cellStr(1, sql);
    sheet.cellDouble(2, sec);
    sheet.row().finish();

  }

  public void finish() {

    sheet.skipRow();
    xlsx.complete(out);

  }
}
