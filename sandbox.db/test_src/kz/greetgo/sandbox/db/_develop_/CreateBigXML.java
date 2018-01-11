package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateBigXML {

  public static void creaTestXML() throws IOException {

    File test = new File("build/bigXML.xml");
    test.getParentFile().mkdirs();
    FileWriter writer = new FileWriter(test);
    StringBuilder sb = new StringBuilder();
    sb.append("<cia>\n");
    for (int i = 0; i < 50; i++) {

      sb.append("<client id=\"" + RND.str(10) + "\">\n");
      sb.append("<surname value=\"" + RND.str(10) + "\"/>\n");
      sb.append("<name value=\"" + RND.str(10) + "\"/>\n");
      sb.append("<patronymic value=\"" + RND.str(10) + "\"/>\n");
      sb.append("<gender value=\"male\"/>\n");
      sb.append("<charm value=\"" + RND.str(10) + "\"/>\n");
      sb.append("<birth value=\"1990-11-11\"/>\n");
      sb.append("<address>\n");
      sb.append("<fact street=\"" + RND.str(20) + "\" house=\"" + RND.str(3) + "\" flat=\"" + RND.str(2) + "\"/>\n");
      sb.append("<register street=\"" + RND.str(20) + " \" house=\"" + RND.str(3) + "\" flat=\"" + RND.str(2) + "\"/>\n");
      sb.append("</address>\n");
      sb.append("<homePhone>" + RND.intStr(8) + "</homePhone>\n");
      sb.append("<mobilePhone>" + RND.intStr(8) + "</mobilePhone>\n");
      sb.append("<mobilePhone>" + RND.intStr(8) + "</mobilePhone>\n");
      sb.append("<mobilePhone>" + RND.intStr(8) + "</mobilePhone>\n");
      sb.append("<workPhone>" + RND.intStr(8) + "</workPhone>\n");
      sb.append("<workPhone>" + RND.intStr(8) + "</workPhone>\n");
      sb.append("</client>\n");

    }
    sb.append("</cia>\n");

    writer.write(sb.toString());

  }

  public static void main(String[] args) throws IOException {
    creaTestXML();
  }
}
