package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientsListInfo;

import java.util.List;

public interface ClientRegister {
    /**
     * Предоставляет общее количество клиентов в БД
     *
     * @return общее количество клиентов в БД
     */
    long getTotalSize(String filterBy, String filterInputs);

    /**
     * Предоставляет список клиентов и общее количество клиентов в БД
     *
     * @param page номер запрашиваемой страницы
     * @param pageSize максимальное количество элементов на странице
     * @return список клиентов с детальной информацией и общее количество клиентов в БД
     */
    List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, String isDesc, int page, int pageSize);

    /**
     * Предоставляет полную информацию о клиенте
     *
     * @param clientsId id запрашиваемого клиента
     * @return полная информация о клиенте
     */
    ClientDetails getClientDetails(String clientsId);

    /**
     * Добавляет нового пользователя
     *
     * @param newClientsInfo данные нового пользователя в виде Json
     * @return возвращает добавленный клиент с присвоенным id
     */
    ClientInfo addClient(String newClientsInfo);

    /**
     *  Удаляет клиента
     *
     * @param clientsId id клиента, которого надо удалить
     */
    void removeClient(String clientsId, int page, int pageSize);

    /**
     *  Обновляет данные клиента
     *
     * @param clientParams новые данные клиента
     */
    ClientInfo updateClient(String clientParams);

    List<String> getCharms();
}
