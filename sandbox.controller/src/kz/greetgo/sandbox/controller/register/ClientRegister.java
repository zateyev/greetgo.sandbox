package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;

import java.util.List;

public interface ClientRegister {
    /**
     * Предоставляет общее количество клиентов в БД
     *
     * @return общее количество клиентов в БД
     */
    long getTotalSize(String filterBy, String filterInput);

    /**
     * Предоставляет список клиентов
     *
     * @param page номер запрашиваемой страницы
     * @param pageSize максимальное количество элементов на странице
     * @return список клиентов
     */
    List<ClientInfo> getClientsList(String filterBy, String filterInputs, String orderBy, boolean isDesc, int page, int pageSize);

    /**
     * Предоставляет полную информацию о клиенте
     *
     * @param clientId id запрашиваемого клиента
     * @return полная информация о клиенте
     */
    ClientDetails getClientDetails(String clientId);

    /**
     * Добавляет нового пользователя
     *
     * @param clientRecordsToSave  записи нового пользователя
     * @return возвращает добавленный клиент с присвоенным id
     */
    ClientInfo addOrUpdateClient(ClientRecordsToSave clientRecordsToSave);

    /**
     *  Удаляет клиента
     *
     * @param clientsId id клиента, которого надо удалить
     */
    void removeClient(String clientsId);
}
