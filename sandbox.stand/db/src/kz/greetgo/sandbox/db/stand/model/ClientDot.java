package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;
import java.util.Map;

//TODO делаем SharmDot с двумя полями: id, name
public class ClientDot {
  public long id;
  public String surname;
  public String lastname;
  public String patronymic;
  public Gender gender;
  public String birthDate;
  public Charm charm;
  public ResidentialAddressInfo residentialAddressInfo;
  public RegistrationAddressInfo registrationAddressInfo;
  public PhoneInfo phoneInfo;

  public int age;
  public long totalAccountBalance;
  public long maxAccountBalance;
  public long minAccountBalance;

  public ClientDetails toClientInfo() {
    ClientDetails ret = new ClientDetails();

    ret.id = id;
    ret.surname = surname;
    ret.lastname = lastname;
    ret.patronymic = patronymic;
    ret.gender = gender;
    ret.birthDate = birthDate;
    ret.charm = charm;

    ret.residentialAddressInfo = new ResidentialAddressInfo();
    ret.residentialAddressInfo.flat = residentialAddressInfo.flat;
    ret.residentialAddressInfo.home = residentialAddressInfo.home;
    ret.residentialAddressInfo.street = residentialAddressInfo.street;

    ret.registrationAddressInfo = new RegistrationAddressInfo();
    ret.registrationAddressInfo.flat = registrationAddressInfo.flat;
    ret.registrationAddressInfo.home = registrationAddressInfo.home;
    ret.registrationAddressInfo.street = registrationAddressInfo.street;

    ret.phoneInfo = new PhoneInfo();
    ret.phoneInfo.home = phoneInfo.home;
    ret.phoneInfo.work = phoneInfo.work;
    ret.phoneInfo.mobile1 = phoneInfo.mobile1;
    ret.phoneInfo.mobile2 = phoneInfo.mobile2;
    ret.phoneInfo.mobile3 = phoneInfo.mobile3;

    return ret;
  }

  public ClientRecord toClientRecord() {
    ClientRecord ret = new ClientRecord();

    ret.id = id;
    ret.fullName = surname + " " + lastname + " " + patronymic;
    ret.age = age;
    ret.charmName = charm.name;
    ret.totalAccountBalance = totalAccountBalance;
    ret.maxAccountBalance = maxAccountBalance;
    ret.minAccountBalance = minAccountBalance;
    //ret.gender = gender.ordinal();

    return ret;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();

    ret.append("ClientDot of ").append(id).append("\n");
    ret.append("surname: ").append(surname).append("\n");
    ret.append("lastname: ").append(lastname).append("\n");
    ret.append("patronymic: ").append(patronymic).append("\n");
    ret.append("gender: ").append(gender.name()).append("\n");
    ret.append("birthdate: ").append(birthDate).append("\n");
    ret.append("charm: ").append(charm.id).append("\n");

    ret.append("residentialAddress: ").append(residentialAddressInfo.street).append(" ");
    ret.append(residentialAddressInfo.home).append(" ");
    ret.append(residentialAddressInfo.flat).append("\n");

    ret.append("registrationAddress: ").append(registrationAddressInfo.street).append(" ");
    ret.append(registrationAddressInfo.home).append(" ");
    ret.append(registrationAddressInfo.flat).append("\n");

    ret.append("phone: ").append(phoneInfo.home).append(" ");
    ret.append(phoneInfo.work).append(" ");
    ret.append(phoneInfo.mobile1).append(" ");
    ret.append(phoneInfo.mobile2).append(" ");
    ret.append(phoneInfo.mobile3).append("\n");

    return ret.toString();
  }
}
