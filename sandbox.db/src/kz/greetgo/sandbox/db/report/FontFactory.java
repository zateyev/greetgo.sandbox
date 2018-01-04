package kz.greetgo.sandbox.db.report;

public class FontFactory {
  public String Times_New_Roman_KZM = "Times_New_Roman_KZM.ttf";
  
  public java.io.InputStream getTimesNewRomanKZM() {
    return getClass().getResourceAsStream(Times_New_Roman_KZM);
  }
}
