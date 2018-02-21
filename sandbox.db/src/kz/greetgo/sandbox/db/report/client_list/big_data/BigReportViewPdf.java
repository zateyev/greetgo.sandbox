package kz.greetgo.sandbox.db.report.client_list.big_data;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.report.BigReportView;
import kz.greetgo.sandbox.controller.report.ReportFootData;
import kz.greetgo.sandbox.controller.report.ReportHeadData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.pdf.PdfWriter;

public class BigReportViewPdf implements BigReportView {

  private static final String DEST = "build/report/Big_report.pdf";
  private static final String FONT_DIR = "fonts/FreeSans.ttf";
  private Font font = FontFactory.getFont(FONT_DIR, "Cp1251", BaseFont.EMBEDDED);
  private PdfPTable table;

  private PrintStream printStream;
  private Document reportPdf;

  public BigReportViewPdf(Document reportPdf) {
    this.reportPdf = reportPdf;
  }

  public BigReportViewPdf(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void start(ReportHeadData headData) throws DocumentException {
    reportPdf.open();
    table = new PdfPTable(6);
    reportPdf.add(new Paragraph("Список клиентов", font));

    table.addCell(new Phrase("ФИО", font));
    table.addCell(new Phrase("Характер", font));
    table.addCell(new Phrase("Возраст", font));
    table.addCell(new Phrase("Общий остаток счетов", font));
    table.addCell(new Phrase("Минимальный остаток", font));
    table.addCell(new Phrase("Максимальный остаток", font));
  }

  @Override
  public void addRow(ClientInfo row) {
    table.addCell(new Phrase(row.surname + " " + row.name + " " + row.patronymic, font));
    table.addCell(new Phrase(row.charm.name, font));
    table.addCell(new Phrase("" + row.age, font));
    table.addCell(new Phrase("" + row.totalBalance, font));
    table.addCell(new Phrase("" + row.minBalance, font));
    table.addCell(new Phrase("" + row.maxBalance, font));
  }

  @Override
  public void finish(ReportFootData footData) throws DocumentException {
    reportPdf.add(table);
    if (footData.generatedAt != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      reportPdf.add(new Paragraph("Дата генерации отчета: " + sdf.format(footData.generatedAt), font));
    }
    reportPdf.close();
  }

  public static void main(String[] args) throws Exception {
    File file = new File(DEST);
    file.getParentFile().mkdirs();

    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      try (PrintStream printStream = new PrintStream(fileOutputStream, false, "UTF-8")) {

        // step 1
        Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(DEST));

        BigReportViewPdf viewPdf = new BigReportViewPdf(document);
        ReportHeadData head = new ReportHeadData();
        head.title = "Мюон";
        viewPdf.start(head);

        for (int i = 0; i < 25; i++) {
          ClientInfo row = new ClientInfo();
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
          viewPdf.addRow(row);
        }

        ReportFootData foot = new ReportFootData();
        foot.generatedBy = "Кристиан Бэйл";
        foot.generatedAt = new Date();
        viewPdf.finish(foot);

      }
    }
  }
}
