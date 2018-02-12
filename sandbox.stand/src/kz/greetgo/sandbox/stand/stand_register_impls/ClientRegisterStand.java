package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
    public BeanGetter<ClientStandDb> clientD;

    @Override
    public ClientsListInfo getClientsList(int page, int pageSize) {

        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());
        List<ClientInfo> clientsList = new ArrayList<>();
        clientDots.forEach(clientDot -> clientsList.add(clientDot.toClientInfo()));

        int totalSize = clientsList.size();
        PageUtils.cutPage(clientsList, page*pageSize, pageSize);

        return new ClientsListInfo(totalSize, clientsList);
    }

    @Override
    public ClientsFullInfo getClientsFullInfo(String clientsId) {
        ClientDot clientDot = clientD.get().clientStorage.get(clientsId);
        return clientDot.toClientsFullInfo();
    }

    @Override
    public ClientsListInfo filterClientsList(String filtersInput, String  filterBy, int page, int pageSize) {

        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());
        List<ClientInfo> clientInfos = new ArrayList<>();
        clientDots.forEach(personDot -> clientInfos.add(personDot.toClientInfo()));

        List<ClientInfo> filteredClients = new ArrayList<>();
        for (ClientInfo client : clientInfos) {
            if ("Фамилия".equals(filterBy) && client.getSurname().contains(filtersInput))
                filteredClients.add(client);
            else if ("Имя".equals(filterBy) && client.getName().contains(filtersInput))
                filteredClients.add(client);
            else if ("Отчество".equals(filterBy) && client.getPatronymic().contains(filtersInput))
                filteredClients.add(client);
        }

        int totalSize = filteredClients.size();
        PageUtils.cutPage(filteredClients, page*pageSize, pageSize);

        return new ClientsListInfo(totalSize, filteredClients);
    }

    @Override
    public ClientsListInfo sortClientsList(String sortBy, String desc, int page, int pageSize) {
        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());
        List<ClientInfo> clientInfos = new ArrayList<>();
        clientDots.forEach(clientDot -> clientInfos.add(clientDot.toClientInfo()));

        if ("age".equals(sortBy))
            clientInfos.sort((Comparator.comparingInt(ClientInfo::getAge)));
        else if ("totalBalance".equals(sortBy))
            clientInfos.sort((Comparator.comparingInt(ClientInfo::getTotalBalance)));
        else if ("minBalance".equals(sortBy))
            clientInfos.sort((Comparator.comparingInt(ClientInfo::getMinBalance)));
        else if ("maxBalance".equals(sortBy))
            clientInfos.sort((Comparator.comparingInt(ClientInfo::getMaxBalance)));

        if (Boolean.valueOf(desc)) Collections.reverse(clientInfos);

        int totalSize = clientInfos.size();
        PageUtils.cutPage(clientInfos, page*pageSize, pageSize);

        return new ClientsListInfo(totalSize, clientInfos);
    }

    @Override
    public ClientInfo addNewClient(String newClientsInfo) {
        JSONObject jsonObject = new JSONObject(newClientsInfo);

        Address addressF = new Address(
                jsonObject.getString("streetF"),
                jsonObject.getString("buildingF"),
                jsonObject.getString("apartmentF")
        );
        Address addressR = new Address(
                jsonObject.getString("streetR"),
                jsonObject.getString("buildingR"),
                jsonObject.getString("apartmentR")
        );

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        int i = 0;
        while (jsonObject.has("phoneType" + i) && jsonObject.has("phoneNumber" + i)) {
            PhoneNumber phoneNumber = new PhoneNumber(PhoneType.valueOf(jsonObject.getString("phoneType" + i)),
                    jsonObject.getString("phoneNumber" + i));
            phoneNumbers.add(phoneNumber);
            i++;
        }
        ClientDot newClient = new ClientDot(
                "p" + (clientD.get().clientStorage.size() + 1),
                jsonObject.getString("surname"),
                jsonObject.getString("name"),
                jsonObject.getString("patronymic"),
                addressF,
                addressR,
                phoneNumbers
        );

        if (jsonObject.has("charm")) {
            newClient.setCharm(jsonObject.getString("charm"));
        }

        if (jsonObject.has("gender")) {
            newClient.setGender(jsonObject.getString("gender"));
        }

        if (!jsonObject.getString("dateOfBirth").isEmpty()) {
            LocalDate dateOfBirth = LocalDate.parse(jsonObject.getString("dateOfBirth"));
            newClient.setDateOfBirth(dateOfBirth);
        }

        clientD.get().clientStorage.put(newClient.getId(), newClient);

        return newClient.toClientInfo();
    }

    @Override
    public void removeClient(String clientsId, int page, int pageSize) {
        clientD.get().clientStorage.remove(clientsId);
    }

    @Override
    public ClientInfo updateClient(String clientParams) {
        JSONObject jsonObject = new JSONObject(clientParams);

        ClientDot clientDot = clientD.get().clientStorage.get(jsonObject.getString("id"));

        if (!jsonObject.getString("surname").isEmpty()) {
            clientDot.setSurname(jsonObject.getString("surname"));
        }
        if (!jsonObject.getString("name").isEmpty()) {
            clientDot.setName(jsonObject.getString("name"));
        }
        if (!jsonObject.getString("patronymic").isEmpty()) {
            clientDot.setPatronymic(jsonObject.getString("patronymic"));
        }
        if (!jsonObject.getString("dateOfBirth").isEmpty()) {
            LocalDate dateOfBirth = LocalDate.parse(jsonObject.getString("dateOfBirth"));
            clientDot.setDateOfBirth(dateOfBirth);
        }
        if (jsonObject.has("charm") && !jsonObject.getString("charm").isEmpty()) {
            clientDot.setCharm(jsonObject.getString("charm"));
        }

        if (jsonObject.has("gender") && !jsonObject.getString("gender").isEmpty()) {
            clientDot.setGender(jsonObject.getString("gender"));
        }
        if (!jsonObject.getString("streetF").isEmpty() &&
                !jsonObject.getString("buildingF").isEmpty() &&
                !jsonObject.getString("apartmentF").isEmpty()) {

            Address addressF = new Address(
                    jsonObject.getString("streetF"),
                    jsonObject.getString("buildingF"),
                    jsonObject.getString("apartmentF")
            );
            clientDot.setAddressF(addressF);
        }
        if (!jsonObject.getString("streetR").isEmpty() &&
                !jsonObject.getString("buildingR").isEmpty() &&
                !jsonObject.getString("apartmentR").isEmpty()) {

            Address addressR = new Address(
                    jsonObject.getString("streetR"),
                    jsonObject.getString("buildingR"),
                    jsonObject.getString("apartmentR")
            );
            clientDot.setAddressR(addressR);
        }
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        int i = 0;
        while (jsonObject.has("phoneType" + i) && jsonObject.has("phoneNumber" + i)) {
            PhoneNumber phoneNumber = new PhoneNumber(PhoneType.valueOf(jsonObject.getString("phoneType" + i)),
                    jsonObject.getString("phoneNumber" + i));
            phoneNumbers.add(phoneNumber);
            i++;
        }
        clientDot.setPhoneNumbers(phoneNumbers);

        clientD.get().clientStorage.put(jsonObject.getString("id"), clientDot);

        return clientDot.toClientInfo();
    }
}
