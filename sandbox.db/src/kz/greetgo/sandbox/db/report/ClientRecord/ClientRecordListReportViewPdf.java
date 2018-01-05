package kz.greetgo.sandbox.db.report.ClientRecord;

import com.google.common.io.ByteStreams;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.report.*;
import kz.greetgo.sandbox.db.report.FontFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClientRecordListReportViewPdf  {

  private static Font font_10 = null;

  public void generate(OutputStream out, List<ClientRecord> in) throws IOException, DocumentException{

    Document pdf = new Document(new Rectangle(842, 595));


    start(pdf, out);

    initFont();

    initContent(pdf, in);

    close(pdf, out);

  }

  private static void close(Document pdf, OutputStream out) throws IOException {
    pdf.close();
    out.close();
  }


  private static void initFont() throws IOException, DocumentException {
    kz.greetgo.sandbox.db.report.FontFactory fontFactory = new FontFactory();
    byte[] ttfAfm = ByteStreams.toByteArray(fontFactory.getTimesNewRomanKZM());
    BaseFont baseFont = BaseFont.createFont(fontFactory.Times_New_Roman_KZM, BaseFont.IDENTITY_H,
      BaseFont.EMBEDDED, BaseFont.NOT_CACHED, ttfAfm, null);

    font_10 = new Font(baseFont, 10, Font.NORMAL);
  }


  private static void initContent(Document pdf, List<ClientRecord> in) throws DocumentException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String titleStr = "Список клиентов на дату " + dateFormat.format(new Date());

    Paragraph head = new Paragraph(titleStr, font_10);
    head.setAlignment(Element.ALIGN_CENTER);
    emptyLine(head, 1);
    pdf.add(head);


    Paragraph para = new Paragraph();
    para.setFont(font_10);
    para.add("\n");

    PdfPTable table = new PdfPTable(6);
    table.setHorizontalAlignment(Element.ALIGN_LEFT);
    table.setWidthPercentage(100);
    table.setWidths(new int[] { 3, 2, 1, 2, 2, 2});

    initTableHeader(table, "ФИО");
    initTableHeader(table, "Характер");
    initTableHeader(table, "Возраст");
    initTableHeader(table, "Общий остаток счетов");
    initTableHeader(table, "Максимальный остаток счетов");
    initTableHeader(table, "Минимальный остаток счетов");

    for (ClientRecord row:
         in) {
      initTableCell(table, row.fio);
      initTableCell(table, row.charm);
      initTableCell(table, readInt(row.age));
      initTableCell(table, readFloat(row.totalAccountBalance));
      initTableCell(table, readFloat(row.maxAccountBalance));
      initTableCell(table, readFloat(row.minAccountBalance));
    }

    para.add(table);
    pdf.add(para);

  }

  private static String readFloat(float balance) {
    Float fFloat = new Float(balance);
    return fFloat.toString();
  }

  private static String readInt(int age) {
    Integer integer = new Integer(age);
    return integer.toString();
  }

  private static void start(Document pdf, OutputStream out) throws DocumentException {
    PdfWriter.getInstance(pdf, out);
    pdf.open();
  }

  private static void emptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(""));
    }
  }

  private static void initTableHeader(PdfPTable table, String text) {
    Phrase phrase = new Phrase(text, font_10);
    PdfPCell cell = new PdfPCell(phrase);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);
  }

  private static void initTableCell(PdfPTable table, String text) {
    Phrase phrase = new Phrase(text, font_10);
    PdfPCell cell = new PdfPCell(phrase);
    table.addCell(cell);
  }



}
