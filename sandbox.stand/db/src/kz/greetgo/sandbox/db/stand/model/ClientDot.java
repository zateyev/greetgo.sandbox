package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public class ClientDot {

  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public String charm;
  public String charmId;
  public String dateOfBirth;
  public float balance;
  ///////////DETAILS
  public String gender;
  public String regStreet, regHouse, regFlat;
  public String factStreet, factHouse, factFlat;
  public String homePhone;
  public String workPhone;
  public List<String> mobilePhone;

  public ClientRecord toClientRecord() {
    ClientRecord rec = new ClientRecord();
    rec.id = this.id;
    rec.fio = this.surname + " " + this.name + " " + this.patronymic;
    rec.charm = this.charm;
    int year = Integer.parseInt(this.dateOfBirth.trim().substring(0, 4));
    rec.age = 2017 - year;
    rec.totalAccountBalance = this.balance;
    rec.maxAccountBalance = this.balance;
    rec.minAccountBalance = this.balance;
    return rec;
  }

  public ClientDetails toClientDetails() {
    ClientDetails rec = new ClientDetails();

    rec.id = this.id;
    rec.name = this.name;
    rec.surname = this.surname;
    rec.patronymic = this.patronymic;
    rec.dateOfBirth = String.valueOf(this.dateOfBirth);
    rec.gender = this.gender;
    rec.charmId = this.charmId;


    CharmRecord charmRecord = new CharmRecord();
    CharmRecord charmRecord2 = new CharmRecord();
    CharmRecord charmRecord3 = new CharmRecord();
    charmRecord.id = "1";
    charmRecord2.id = "2";
    charmRecord3.id = "3";
    charmRecord.name = "Хорош";
    charmRecord2.name = "Плохо";
    charmRecord3.name = "Отлично";


    rec.charms.add(charmRecord);
    rec.charms.add(charmRecord2);
    rec.charms.add(charmRecord3);

    ClientPhones phones = new ClientPhones();
    ClientAddress fact = new ClientAddress();
    ClientAddress reg = new ClientAddress();

    phones.home = this.homePhone;
    phones.work = this.workPhone;
    phones.mobile = this.mobilePhone;


    fact.street = factStreet;
    fact.house = factHouse;
    fact.flat = factFlat;


    reg.street = regStreet;
    reg.house = regFlat;
    reg.flat = regFlat;

    rec.factAddress = fact;
    rec.regAddress = reg;
    rec.phones = phones;

    return rec;
  }

}
