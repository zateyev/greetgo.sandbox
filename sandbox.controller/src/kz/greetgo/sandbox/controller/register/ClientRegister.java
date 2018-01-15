package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.CharmType;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.List;
import java.util.Map;

public interface ClientRegister {
  /**
   * Посылает информацию о характере с его эквивалентами на уже определенном языке с учитыванием полов
   *
   * @return словарь с кодом характера и списком строковых значения для каждого пола
   */
  Map<Integer, List<String>> getCharmData();

  /**
   * Предоставляет количество страниц клиентских записей
   *
   * @param clientRecordCount количество клиентских записей в одной странице
   * @return количество страниц
   */
  long getPageCount(long clientRecordCount);

  /**
   * Предоставляет список клиентских записей
   *
   * @param clientRecordCountToSkip количество пропускаемых клиентских записей (с начала)
   * @param clientRecordCount количество клиентских записей в одной странице
   * @return список клиентских записей
   */
  List<ClientRecord> getClientRecordList(long clientRecordCountToSkip, long clientRecordCount);

  /**
   * Возвращает успешное удаление клиентской записи
   *
   * @param clientRecordId идентификатор клиентской записи
   * @return успешное удаление
   */
  boolean removeClientRecord(long clientRecordId);
}
