package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.RequestParameters;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {
    public BeanGetter<ClientStandDb> clientD;

    @Override
    public long getTotalSize(RequestParameters requestParams) {
        return filterClientsList(requestParams.filterBy, requestParams.filterInput).size();
    }

    @Override
    public List<ClientInfo> getClientsList(RequestParameters requestParams) {
        List<ClientInfo> clientInfos = filterClientsList(requestParams.filterBy, requestParams.filterInput);
        if (clientInfos.isEmpty()) return clientInfos;


        String sortBy = requestParams.orderBy != null ? requestParams.orderBy.trim() : "";
        if ("age".equals(sortBy))
            clientInfos.sort(Comparator.comparingInt(o -> o.age));
        else if ("totalBalance".equals(sortBy))
            clientInfos.sort(Comparator.comparingDouble(o -> o.totalBalance));
        else if ("minBalance".equals(sortBy))
            clientInfos.sort(Comparator.comparingDouble(o -> o.minBalance));
        else if ("maxBalance".equals(sortBy))
            clientInfos.sort(Comparator.comparingDouble(o -> o.maxBalance));
        else
            clientInfos.sort(Comparator.comparing(o -> o.surname));

        if (requestParams.isDesc) Collections.reverse(clientInfos);


        PageUtils.cutPage(clientInfos, requestParams.page*requestParams.pageSize, requestParams.pageSize);

        return clientInfos;
    }

    private List<ClientInfo> filterClientsList(String filterBy, String filterInput) {
        List<ClientInfo> clientsList = new ArrayList<>();
        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());

        if (filterBy != null && !filterBy.isEmpty() && filterInput != null && !filterInput.isEmpty()) {
            for (ClientDot clientDot : clientDots) {
                if ("surname".equals(filterBy) && clientDot.getSurname().toLowerCase().contains(filterInput.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("name".equals(filterBy) && clientDot.getName().toLowerCase().contains(filterInput.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("patronymic".equals(filterBy) && clientDot.getPatronymic() != null && clientDot.getPatronymic().toLowerCase().contains(filterInput.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
            }

        } else {
            clientDots.forEach(clientDot -> clientsList.add(clientDot.toClientInfo()));
        }
        return clientsList;
    }

    @Override
    public ClientDetails getClientDetails(String clientId) {
        ClientDot clientDot = clientD.get().clientStorage.get(clientId);
        return clientDot.toClientDetails();
    }

    @Override
    public ClientInfo addOrUpdateClient(ClientRecordsToSave clientRecordsToSave) {

        if (clientRecordsToSave.id != null) {
            ClientDot clientDot = clientD.get().clientStorage.get(clientRecordsToSave.id);
            if (clientDot != null) {
                clientRecordsToSave.charm = clientD.get().charmStorage.get(clientRecordsToSave.charm.id);
                clientDot.saveRecords(clientRecordsToSave);
                return clientDot.toClientInfo();
            }
            return null;
        }

        if (clientRecordsToSave.surname == null) return null;

        clientRecordsToSave.id = "p" + (clientD.get().clientStorage.size() + 1);
        clientRecordsToSave.charm = clientD.get().charmStorage.get(clientRecordsToSave.charm.id);
        ClientDot clientDot = new ClientDot(clientRecordsToSave);
        clientD.get().clientStorage.put(clientDot.getId(), clientDot);

        return clientDot.toClientInfo();
    }

    @Override
    public void removeClient(String clientsId) {
        clientD.get().clientStorage.remove(clientsId);
    }
}
