package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordListRequest;

import java.util.List;
import java.util.Map;

public interface ClientRegister {
  /**
   * Предоставляет количество страниц клиентских записей
   *
   * @param clientRecordCount количество клиентских записей в одной странице
   * @param nameFilter        текущая применяемая фильтрация
   * @return количество страниц
   */
  long getPageCount(long clientRecordCount, String nameFilter);

  /**
   * Предоставляет список клиентских записей
   *
   * @param clientRecordListRequest принимаемые параметры страницы, сортировки и фильтрации в виде модели
   * @return список клиентских записей
   */
  List<ClientRecord> getClientRecordList(ClientRecordListRequest clientRecordListRequest);

  /**
   * Удаление клиентской записи
   *
   * @param clientRecordId идентификатор клиентской записи
   */
  void removeClientDetails(long clientRecordId);

  /**
   * Возвращает клиентские детали
   *
   * @param clientRecordId идентификатор клиентской записи
   * @return клиентский детали одного клиента
   */
  ClientDetails getClientDetails(Long clientRecordId);

  /**
   * Сохранение новых или изменение существующих клиентских деталей
   *
   * @param clientDetailsToSave клиентские детали
   */
  void saveClientDetails(ClientDetailsToSave clientDetailsToSave);
}
