package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecords;
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
    public long getTotalSize(String filterBy, String filterInput) {
        return filterClientsList(filterBy, filterInput).size();
    }

    @Override
    public List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, boolean isDesc, int page, int pageSize) {
        List<ClientInfo> clientInfos = filterClientsList(filterBy, filterInputs);
        if (clientInfos.isEmpty()) return clientInfos;


        String sortBy = orderBy != null ? orderBy.trim() : "";
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

        if (isDesc) Collections.reverse(clientInfos);


        PageUtils.cutPage(clientInfos, page*pageSize, pageSize);

        return clientInfos;
    }

    private List<ClientInfo> filterClientsList(String filterBy, String filterInputs) {
        List<ClientInfo> clientsList = new ArrayList<>();
        List<ClientDot> clientDots = new ArrayList<>(clientD.get().clientStorage.values());

        if (filterBy != null && filterInputs != null) {
            for (ClientDot clientDot : clientDots) {
                if ("surname".equals(filterBy) && clientDot.getSurname().toLowerCase().contains(filterInputs.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("name".equals(filterBy) && clientDot.getName().toLowerCase().contains(filterInputs.toLowerCase()))
                    clientsList.add(clientDot.toClientInfo());
                else if ("patronymic".equals(filterBy) && clientDot.getPatronymic().toLowerCase().contains(filterInputs.toLowerCase()))
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
    public ClientInfo addOrUpdateClient(ClientRecords clientRecords) {

        if (clientRecords.id != null) {
            ClientDot clientDot = clientD.get().clientStorage.get(clientRecords.id);
            if (clientDot != null) {
                clientDot.saveRecords(clientRecords);
                return clientDot.toClientInfo();
            }
            return null;
        }

        if (clientRecords.surname == null) return null;

        clientRecords.id = "p" + (clientD.get().clientStorage.size() + 1);
        ClientDot clientDot = new ClientDot(clientRecords);
        clientD.get().clientStorage.put(clientDot.getId(), clientDot);

        return clientDot.toClientInfo();
    }

    @Override
    public void removeClient(String clientsId) {
        clientD.get().clientStorage.remove(clientsId);
    }
}
