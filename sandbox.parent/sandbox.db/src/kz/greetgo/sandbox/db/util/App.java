package kz.greetgo.sandbox.db.util;

public class App {
  public static String appDir() {
    return System.getProperty("user.home") + "/sandbox.d";
  }

  public static String securityDir() {
    return appDir() + "/security";
  }
}
