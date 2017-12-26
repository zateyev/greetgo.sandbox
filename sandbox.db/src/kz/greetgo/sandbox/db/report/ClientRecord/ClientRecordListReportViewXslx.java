package kz.greetgo.sandbox.db.report.ClientRecord;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientRecordListReportViewXslx implements ClientRecordListReportView {
  private OutputStream out;

  public ClientRecordListReportViewXslx(OutputStream out) {
    this.out = out;
  }

  private final Xlsx xlsx = new Xlsx();
  private Sheet sheet;


  @Override
  public void start(Date onDate) {
    sheet = xlsx.newSheet(true);

    sheet.setWidth(1, 45);
    sheet.setWidth(1, 25);
    sheet.setWidth(1, 10);
    sheet.setWidth(1, 20);
    sheet.setWidth(1, 20);
    sheet.setWidth(1, 20);

    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

    sheet.row().height(27.75).start();
    sheet.cellStr(2, "Список клиентов на дату: " + f.format(onDate));
    sheet.row().finish();
    sheet.skipRow();

    sheet.row().start();
    sheet.style().font().bold();
    sheet.cellStr(1, "ФИО клиента");
    sheet.cellStr(2, "Характер");
    sheet.cellStr(3, "Возраст");
    sheet.cellStr(4, "Общий остаток счетов");
    sheet.cellStr(5, "Максимальный остаток");
    sheet.cellStr(6, "Минимальный остаток");
    sheet.style().clean();
    sheet.row().finish();
  }

  @Override
  public void append(ClientRecord row) {
    sheet.row().start();
    sheet.cellStr(1, row.fio);
    sheet.cellStr(2, row.charm);
    sheet.cellInt(3, row.age);
    sheet.cellStr(4, String.valueOf(row.totalAccountBalance));
    sheet.cellStr(5, String.valueOf(row.maxAccountBalance));
    sheet.cellStr(6, String.valueOf(row.minAccountBalance));
    sheet.row().finish();
  }

  @Override
  public void finish() {
    xlsx.complete(out);
  }
}
