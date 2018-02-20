package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class ClientStandDb implements HasAfterInject {
    public final Map<String, ClientDot> clientStorage = new HashMap<>();
    public final Map<String, Charm> charmStorage = new HashMap<>();

    @Override
    public void afterInject() throws Exception {
        String[] charmNames = {"Уситчивый", "Агрессивный", "Спокойный", "Грубый", "Тактичный"};

        for (int i = 0; i < charmNames.length; i++) {
            Charm charm = new Charm();
            charm.id = "ch" + i;
            charm.name = charmNames[i];

            this.charmStorage.put(charm.id, charm);
        }

        Random rand = new Random(System.currentTimeMillis());

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("ClientStandDbInitData.txt"), "UTF-8"))) {

            int lineNo = 0;

            while (true) {
                String line = br.readLine();
                if (line == null) break;
                lineNo++;
                String trimmedLine = line.trim();
                if (trimmedLine.length() == 0) continue;
                if (trimmedLine.startsWith("#")) continue;

                String[] splitLine = line.split(";");

                String command = splitLine[0].trim();
                switch (command) {
                    case "CLIENT":
                        appendClient(splitLine, line, lineNo, charmStorage.get("ch" + rand.nextInt(charmStorage.size())));
                        break;

                    default:
                        throw new RuntimeException("Unknown command " + command);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void appendClient(String[] splitLine, String line, int lineNo, Charm charm) {
        ClientDot client = new ClientDot();
        client.setCharm(charm);
        client.setId(splitLine[1].trim());
        String[] ap = splitLine[2].trim().split("\\s+");
        String[] fio = splitLine[3].trim().split("\\s+");
        client.setMinBalance(Integer.parseInt(ap[0]));
        client.setMaxBalance(Integer.parseInt(ap[1]));
        client.setSurname(fio[0]);
        client.setName(fio[1]);
        if (fio.length > 2) client.setPatronymic(fio[2]);
        clientStorage.put(client.getId(), client);
    }
}
