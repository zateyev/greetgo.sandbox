package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientsFullInfo;
import kz.greetgo.sandbox.controller.model.ClientsListInfo;

public interface ClientRegister {
    /**
     * Предоставляет список клиентов
     *
     * @param page номер запрашиваемой страницы
     * @param pageSize максимальное количество элементов на странице
     * @return список клиентов с детальной информацией
     */
    ClientsListInfo getClientsList(int page, int pageSize);

    /**
     * Предоставляет полную информацию о клиенте
     *
     * @param clientsId id запрашиваемого клиента
     * @return полная информация о клиенте
     */
    ClientsFullInfo getClientsFullInfo(String clientsId);

    /**
     * Предоставляет отфильтрованный список клиентов
     *
     * @param filtersInput входные данные для фильтрации
     * @param filterBy фильтровать по (напр. Фамилия, Имя, Отчество)
     * @return отфильтрованный список клиентов с детальной информацией
     */
    ClientsListInfo filterClientsList(String filtersInput, String  filterBy, int page, int pageSize);

    /**
     * Сортирует список клиентов
     *
     * @param sortBy параметр по которому нужно сортировать
     * @param desc если true, то сортировать по убыванию
     * @return отсортированный список клиентов с детальной информацией
     */
    ClientsListInfo sortClientsList(String sortBy, String desc, int page, int pageSize);

    /**
     * Добавляет нового пользователя
     *
     * @param newClientsInfo данные нового пользователя в виде Json
     * @return возвращает добавленный клиент с присвоенным id
     */
    ClientInfo addNewClient(String newClientsInfo);

    /**
     *  Удаляет клиента
     *
     * @param clientsId id клиента, которого надо удалить
     */
    void removeClient(String clientsId, int page, int pageSize);

    ClientInfo updateClient(String clientParams);
}
