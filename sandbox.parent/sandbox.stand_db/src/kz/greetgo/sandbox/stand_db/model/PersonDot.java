package kz.greetgo.sandbox.stand_db.model;

import kz.greetgo.sandbox.controller.model.UserInfo;

public class PersonDot {
  public String id;
  public String accountName;
  public boolean disabled = false;
  public String surname, name, patronymic;

  public UserInfo toUserInfo() {
    UserInfo ret = new UserInfo();
    ret.id = id;
    ret.accountName = accountName;
    ret.surname = surname;
    ret.name = name;
    ret.patronymic = patronymic;
    return ret;
  }
}
