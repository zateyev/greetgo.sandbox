package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientsListInfo {
    private int totalClientsNumber;
    private List<UserInfo> clients;

    public int getTotalClientsNumber() {
        return totalClientsNumber;
    }

    public void setTotalClientsNumber(int totalClientsNumber) {
        this.totalClientsNumber = totalClientsNumber;
    }

    public List<UserInfo> getClients() {
        return clients;
    }

    public void setClients(List<UserInfo> clients) {
        this.clients = clients;
    }

    public ClientsListInfo(int totalNumber, List<UserInfo> clients) {
        this.totalClientsNumber = totalNumber;
        this.clients = clients;
    }
}
