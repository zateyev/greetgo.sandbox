package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.List;

public interface ClientRegister {
  /**
   * Предоставляет список клиентских записей
   *
   * @param clientRecordCountToSkip количество пропускаемых клиентских записей (с начала)
   * @param clientRecordCount количество клиентских записей в одной странице
   * @return список клиентских записей
   */
  List<ClientRecord> getClientRecordList(int clientRecordCountToSkip, int clientRecordCount);

  /**
   * Предоставляет количество страниц клиентских записей
   *
   * @param clientRecordCount количество клиентских записей в одной странице
   * @return количество страниц
   */
  int getPageCount(int clientRecordCount);
}
