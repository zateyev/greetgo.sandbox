package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;

import java.util.List;
import java.util.Map;

public interface ClientRegister {
  /**
   * Предоставляет количество страниц клиентских записей
   *
   * @param clientRecordCount количество клиентских записей в одной странице
   * @return количество страниц
   */
  long getPageCount(long clientRecordCount);

  /**
   * Предоставляет список клиентских записей
   *TODO: доделать java-help
   * @return список клиентских записей
   */
  List<ClientRecord> getClientRecordList(ClientRecordListRequest clientRecordListRequest);

  /**
   * Возвращает успешное удаление клиентской записи
   *
   * @param clientRecordId идентификатор клиентской записи
   * @return успешное удаление
   */
  boolean removeClientRecord(long clientRecordId);
}
