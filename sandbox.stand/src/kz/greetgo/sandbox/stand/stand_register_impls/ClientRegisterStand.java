package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;

@Bean
public class ClientRegisterStand implements ClientRegister {
    public BeanGetter<ClientStandDb> clientD;

    @Override
    public long getTotalSize(String filterBy, String filterInputs) {
        return filterClientsList(filterBy, filterInputs).size();
    }

    @Override
    public List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, String isDesc, int page, int pageSize) {
        List<ClientInfo> clientsList = filterClientsList(filterBy, filterInputs);
        if (clientsList.isEmpty()) return clientsList;


        String sortBy = orderBy != null ? orderBy.trim() : "";
        if ("age".equals(sortBy))
            clientsList.sort((Comparator.comparingInt(ClientInfo::getAge)));
        else if ("totalBalance".equals(sortBy))
            clientsList.sort((Comparator.comparingInt(ClientInfo::getTotalBalance)));
        else if ("minBalance".equals(sortBy))
            clientsList.sort((Comparator.comparingInt(ClientInfo::getMinBalance)));
        else if ("maxBalance".equals(sortBy))
            clientsList.sort((Comparator.comparingInt(ClientInfo::getMaxBalance)));
        else
            clientsList.sort(Comparator.comparing(ClientInfo::getSurname));

        if (Boolean.valueOf(isDesc)) Collections.reverse(clientsList);


        PageUtils.cutPage(clientsList, page*pageSize, pageSize);

        return clientsList;
    }

    private List<ClientInfo> filterClientsList(String filterBy, String filterInputs) {
        List<ClientInfo> clientsList = new ArrayList<>();
        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());

        if (filterBy != null && filterInputs != null) {
            for (ClientDot clientDot : clientDots) {
                if ("Фамилия".equals(filterBy) && clientDot.getSurname().toLowerCase().contains(filterInputs.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("Имя".equals(filterBy) && clientDot.getName().toLowerCase().contains(filterInputs.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("Отчество".equals(filterBy) && clientDot.getPatronymic().toLowerCase().contains(filterInputs.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
            }

        } else {
            clientDots.forEach(clientDot -> clientsList.add(clientDot.toClientInfo()));
        }
        return clientsList;
    }

    @Override
    public ClientDetails getClientDetails(String clientsId) {
        ClientDot clientDot = clientD.get().clientStorage.get(clientsId);
        return clientDot.toClientsFullInfo();
    }

    @Override
    public ClientInfo addClient(String newClientsInfo) {
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

    @Override
    public List<String> getCharms() {
        List<String> charmNamesList = new ArrayList<>();
        List<CharmDot> charms = new ArrayList<>(clientD.get().charmStorage.values());
        charms.forEach(charmDot -> charmNamesList.add(charmDot.getName()));
        return charmNamesList;
    }
}
