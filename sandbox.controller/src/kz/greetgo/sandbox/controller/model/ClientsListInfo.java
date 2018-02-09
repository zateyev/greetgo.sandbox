package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientsListInfo {
    private int totalClientsNumber;
    private List<ClientInfo> clients;

    public int getTotalClientsNumber() {
        return totalClientsNumber;
    }

    public void setTotalClientsNumber(int totalClientsNumber) {
        this.totalClientsNumber = totalClientsNumber;
    }

    public List<ClientInfo> getClients() {
        return clients;
    }

    public void setClients(List<ClientInfo> clients) {
        this.clients = clients;
    }

    public ClientsListInfo(int totalNumber, List<ClientInfo> clients) {
        this.totalClientsNumber = totalNumber;
        this.clients = clients;
    }
}
