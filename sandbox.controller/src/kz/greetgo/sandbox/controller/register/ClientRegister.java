package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientListInfo;

import java.util.List;

public interface ClientRegister {
  /**
   * Предоставляет список общей информации о клиентах
   *
   * @param page номер страницы
   * @param size размер страницы
   * @return список общей информации о клиенте
   */
  List<ClientListInfo> getClientList(Integer page, Integer size);

  /**
   * Предоставляет количество страниц
   *
   * @param size размер страницы
   * @return количество страниц
   */
  Integer getPageNum(Integer size);
}
