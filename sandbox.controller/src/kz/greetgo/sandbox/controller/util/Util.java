package kz.greetgo.sandbox.controller.util;

import kz.greetgo.util.RND;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Locale;

public class Util {
  public static String floatToString(float f) {
    return String.format(Locale.US, "%f", f);
  }

  public static float stringToFloat(String s) {
    return Float.parseFloat(s);
  }

  public static LocalDate generateLocalDate() {
    return LocalDate.ofEpochDay(RND.plusLong(LocalDate.now().toEpochDay()));
  }

  public static Date generateDate() {
    return Date.valueOf(generateLocalDate());
  }
}
