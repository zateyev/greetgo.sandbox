package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientRecordsToSave;
import kz.greetgo.sandbox.controller.model.RequestParameters;

import java.util.List;

public interface ClientRegister {
    /**
     * Предоставляет общее количество клиентов в БД
     *
     * @return общее количество клиентов в БД
     */
    long getTotalSize(RequestParameters requestParams);

    /**
     * Предоставляет список клиентов
     *
     * @param requestParams параметры запроса
     * @return список клиентов
     */
    List<ClientInfo> getClientsList(RequestParameters requestParams);

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
