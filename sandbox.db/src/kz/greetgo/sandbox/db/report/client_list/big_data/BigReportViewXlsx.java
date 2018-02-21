package kz.greetgo.sandbox.db.report.client_list.big_data;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportFootData;
import kz.greetgo.sandbox.controller.report.ReportHeadData;
import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class BigReportViewXlsx implements BigReportView {
  private final Xlsx xlsx = new Xlsx();
  private OutputStream out;
  private Sheet sheet;
  private int no = 1;

  public BigReportViewXlsx(OutputStream out) {
    this.out = out;
  }

  public static void main(String[] args) throws Exception {
    String dir = "build/report/";
    String filename = "clientList.xlsx";

    File file = new File(dir + filename);
    file.getParentFile().mkdirs();

    OutputStream out = new FileOutputStream(file);
    BigReportViewXlsx v = new BigReportViewXlsx(out);

    v.start(new ReportHeadData());

    for (int i = 1; i < 100; i++) {
      ClientInfo row = new ClientInfo();
      {
        row.id = "id" + i;
        row.surname = "Surname" + i;
        row.name = "Name" + i;
        row.patronymic = "Patronymic" + i;
        row.charm = new Charm();
        row.charm.name = "Charm" + i;
        row.age = i;
        row.totalBalance = i * i;
        row.minBalance = i * i / 2;
        row.maxBalance = 2 * i * i;
      }
      v.addRow(row);
    }

    ReportFootData foot = new ReportFootData();
    foot.generatedAt = new Date();
    v.finish(foot);

    out.close();

    System.out.println("Complete");
  }

  @Override
  public void start(ReportHeadData headData) {
    sheet = xlsx.newSheet(true);

    sheet.setWidth(1, 4.14);
    sheet.setWidth(2, 36.71);
    sheet.setWidth(3, 13.86);
    sheet.setWidth(4, 22.29);
    sheet.setWidth(5, 14.43);
    sheet.setWidth(6, 17.57);

//    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
//
//    sheet.row().height(27.75).start();
//    sheet.style().font().bold();
//    sheet.style().font().setSize(17);
//    sheet.cellStr(2, "Список договоров на дату: " + f.format(onDate));
//    sheet.style().clean();
//    sheet.row().finish();

    sheet.skipRow();

    sheet.row().start();
    sheet.style().font().bold();
    sheet.cellStr(1, "ФИО");
    sheet.cellStr(2, "Характер");
    sheet.cellStr(3, "Возраст");
    sheet.cellStr(4, "Общий остаток счетов");
    sheet.cellStr(5, "Минимальный остаток");
    sheet.cellStr(6, "Максимальный остаток");
    sheet.style().clean();
    sheet.row().finish();
  }

  @Override
  public void addRow(ClientInfo row) {
    sheet.row().start();
//    sheet.cellInt(1, no++);
    if (row.surname == null) row.surname = "";
    if (row.name == null) row.name = "";
    if (row.patronymic == null) row.patronymic = "";
    sheet.cellStr(1, row.surname + ' ' + row.name + ' ' + row.patronymic);
    sheet.cellStr(2, row.charm.name);
    sheet.cellStr(3, "" + row.age);
    sheet.cellStr(4, "" + row.totalBalance);
    sheet.cellStr(5, "" + row.minBalance);
    sheet.cellStr(6, "" + row.maxBalance);
    sheet.row().finish();
  }

  @Override
  public void finish(ReportFootData footData) {
    xlsx.complete(out);
  }
}
