package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.db.util.ClientRecordParser;
import kz.greetgo.sandbox.db.util.TimeUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.SAXException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Migration {
  private String tmpClientTable;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    // create tmp tables

    int recordsSize = download();

    {
      long now = System.nanoTime();
      info("Downloading of portion " + recordsSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    if (recordsSize == 0) return 0;

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + recordsSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    return recordsSize;
  }

  private long migrateFromTmp() {
    return 0;
  }

  public static void main(String[] args) throws Exception {
    Migration migration = new Migration();
    migration.download();
  }

  private int download() throws IOException, SAXException {
    // get file, read all files iteratively
    InputStream fis = new FileInputStream("build/out_files/from_cia_2018-02-27-154753-1-300.xml.tar.bz2");
    TarArchiveInputStream tarInput = new TarArchiveInputStream(new BZip2CompressorInputStream(fis));
    TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
    BufferedReader br;
    StringBuilder sb = new StringBuilder();
    while (currentEntry != null) {
      br = new BufferedReader(new InputStreamReader(tarInput));
      System.out.println("For File = " + currentEntry.getName());
      String line;
      ClientRecordParser clientRecordParser;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
      currentEntry = tarInput.getNextTarEntry();
    }
    String xmlContent = sb.toString();
//    System.out.println(xmlContent);

    // parse xml
    ClientRecordParser clientRecordParser = new ClientRecordParser();
    clientRecordParser.parseRecordData(xmlContent);

    List<ClientRecordsToSave> clientRecords = clientRecordParser.getClientRecords();
    System.out.println(clientRecords.size());
    for (ClientRecordsToSave clientRecord : clientRecords) {
      System.out.println("surname: " + clientRecord.surname);
      System.out.println("name: " + clientRecord.name);
      System.out.println("dateOfBirth: " + clientRecord.dateOfBirth);
      System.out.println();
    }

    // write into tmp db


    return 0;
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
