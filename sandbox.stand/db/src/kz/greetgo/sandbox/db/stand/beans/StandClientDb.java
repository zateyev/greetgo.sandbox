package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jgolibzhan on 11/30/17.
 */
@Bean
public class StandClientDb implements HasAfterInject {

  public final ArrayList<ClientDot> clientStorage = new ArrayList<>();
  public final HashMap<String, ClientDot> clStorage = new HashMap<>();

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
            appendClients(splitLine);
            break;
          case "2":
            appendClient(splitLine);
            break;
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void appendClients(String[] splitLine) {
    ClientDot d = new ClientDot();
    d.id = splitLine[1].trim();
    String[] fio = splitLine[2].trim().split("\\s+");
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

  private void appendClient(String[] splitLine){
    ClientDot d = new ClientDot();
    d.id = splitLine[1].trim();
    String[] fio = splitLine[2].trim().split("\\s+");
    String phone = splitLine[3].trim();
    d.name = fio[0];
    d.surname = fio[1];
    d.patronymic = fio[2];
    d.phone = phone;
    clStorage.put(d.id, d);
  }
}
