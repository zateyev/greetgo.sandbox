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
   * @param nameFilter текущая применяемая фильтрация
   * @return количество страниц
   */
  long getCount(String nameFilter);

  /**
   * Предоставляет список клиентских записей
   *
   * @param listRequest принимаемые параметры страницы, сортировки и фильтрации в виде модели
   * @return список клиентских записей
   */
  List<ClientRecord> getRecordList(ClientRecordListRequest listRequest);

  /**
   * Удаление клиентской записи
   *
   * @param id идентификатор клиентской записи
   */
  void removeRecord(long id);

  /**
   * Возвращает клиентские детали
   *
   * @param id идентификатор клиентской записи
   * @return клиентский детали одного клиента
   */
  ClientDetails getDetails(Long id);

  /**
   * Сохранение новых или изменение существующих клиентских деталей
   *
   * @param detailsToSave клиентские детали
   */
  void saveDetails(ClientDetailsToSave detailsToSave);
}
