package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Bean
public class ClientStandDb implements HasAfterInject {
    public final Map<String, ClientDot> clientStorage = new HashMap<>();

    @Override
    public void afterInject() throws Exception {
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
                        appendClient(splitLine, line, lineNo);
                        break;

                    default:
                        throw new RuntimeException("Unknown command " + command);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void appendClient(String[] splitLine, String line, int lineNo) {
        ClientDot client = new ClientDot();
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
