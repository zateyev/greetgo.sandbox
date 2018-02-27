package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Migration {
  private String tmpClientTable;

  public long migrate() {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    // create tmp tables

    long fileSize = download();

    {
      long now = System.nanoTime();
      info("Downloading of file with size " + fileSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    if (fileSize == 0) return 0;

    long recordsSize = migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + recordsSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    return fileSize;
  }

  private long migrateFromTmp() {
    return 0;
  }

  private long download() {
    return 0;
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
