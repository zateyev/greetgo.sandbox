package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.PhoneType;

public class UserInfo {
  public String id;
  public String accountName;
  public String surname;
  public String name;
  public String patronymic;
  public PhoneType phoneType;
  public String charm;
  public int age;
  public int totalBalance;
  public int minBalance;
  public int maxBalance;

  public UserInfo(String id,
                  String surname,
                  String name,
                  String patronymic,
                  String charm,
                  int age,
                  int totalBalance,
                  int minBalance,
                  int maxBalance) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.charm = charm;
    this.age = age;
    this.totalBalance = totalBalance;
    this.minBalance = minBalance;
    this.maxBalance = maxBalance;
  }

  public UserInfo() {

  }
}
