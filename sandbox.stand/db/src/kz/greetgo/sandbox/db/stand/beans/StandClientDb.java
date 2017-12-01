package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by jgolibzhan on 11/30/17.
 */
@Bean
public class StandClientDb implements HasAfterInject {

  public final ArrayList<ClientDot> clientStorage = new ArrayList<>();

  @Override
  public void afterInject() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("ListData.txt"), "UTF-8"))) {


      while (true) {
        String line = br.readLine();
        if (line == null) break;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");
        String command = splitLine[0].trim();
        switch (command) {
          case "1":
            appendClient(splitLine, line);
            break;

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void appendClient(String[] splitLine, String line) {
    ClientDot d = new ClientDot();
    d.id = splitLine[1].trim();
    String[] fio = splitLine[2].trim().split("\\s+");
    System.out.println("fio.lentgth" + fio.length);
    switch (fio.length) {
      case 1:
        d.name = fio[0];
        d.surname = "";
        d.patronymic = "";
        break;
      case 2:
        d.name = fio[0];
        d.surname = fio[1];
        d.patronymic = "";
        break;
      case 3:
        d.name = fio[0];
        d.surname = fio[1];
        d.patronymic = fio[2];
        break;
      default:
        d.name = "";
        d.patronymic = "";
        d.surname = "";
    }
    clientStorage.add(d);
  }
}
