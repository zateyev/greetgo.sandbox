package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Bean
public class StandClientDb implements HasAfterInject {

  public final HashMap<String, ClientDot> clientStorage = new HashMap<>();


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
            appendClient(splitLine);
            break;
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  @SuppressWarnings("unused")
  private void appendClient(String[] splitLine) {
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
    d.gender = splitLine[3].trim();
    d.charm = splitLine[4].trim();
    d.dateOfBirth = splitLine[5].trim();
    d.balance = Float.parseFloat(splitLine[6].trim());

    String phones[] = splitLine[7].trim().split("\\s+");

    d.homePhone = phones[0];
    d.workPhone = phones[1];
    d.mobilePhone = new ArrayList<>();
    d.mobilePhone.add(phones[2]);

    String regAddress[] = splitLine[8].trim().split("\\s+");
    d.regStreet = regAddress[0];
    d.regHouse = regAddress[1];
    d.regFlat = regAddress[2];
    String factAddress[] = splitLine[8].trim().split("\\s+");
    d.factStreet = factAddress[0];
    d.factHouse = factAddress[1];
    d.factFlat = factAddress[2];

    d.charmId = splitLine[9].trim();

    clientStorage.put(d.id, d);
  }
}
