package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

import java.util.*;

public class ClientDot {
  public long id;
  public String surname;
  public String name;
  public String patronymic;
  public Gender gender;
  public String birthDate;
  public Charm charm;
  public AddressInfo factualAddressInfo;
  public AddressInfo registrationAddressInfo;
  public List<Phone> phones;

  public int age;
  public String totalAccountBalance;
  public String maxAccountBalance;
  public String minAccountBalance;

  public ClientDetails toClientDetails() {
    ClientDetails ret = new ClientDetails();

    ret.id = id;
    ret.surname = surname;
    ret.name = name;
    ret.patronymic = patronymic;
    ret.gender = gender;
    ret.birthdate = birthDate;
    ret.charmId = charm.id;

    ret.factualAddressInfo = new AddressInfo();
    ret.factualAddressInfo.type = AddressType.FACTUAL;
    ret.factualAddressInfo.flat = factualAddressInfo.flat;
    ret.factualAddressInfo.house = factualAddressInfo.house;
    ret.factualAddressInfo.street = factualAddressInfo.street;

    ret.registrationAddressInfo = new AddressInfo();
    ret.registrationAddressInfo.type = AddressType.REGISTRATION;
    ret.registrationAddressInfo.flat = registrationAddressInfo.flat;
    ret.registrationAddressInfo.house = registrationAddressInfo.house;
    ret.registrationAddressInfo.street = registrationAddressInfo.street;

    ret.phones.addAll(phones);

    return ret;
  }

  public ClientRecord toClientRecord() {
    ClientRecord ret = new ClientRecord();

    ret.id = id;
    ret.fullName = surname + " " + name + " " + patronymic;
    ret.age = age;
    ret.charmName = charm.name;
    ret.totalAccountBalance = totalAccountBalance;
    ret.maxAccountBalance = maxAccountBalance;
    ret.minAccountBalance = minAccountBalance;

    return ret;
  }

  public void toClientDot(ClientDetailsToSave clientDetailsToSave, Long id, Map<Integer, CharmDot> charmStorage) {
    surname = clientDetailsToSave.surname;
    name = clientDetailsToSave.name;
    patronymic = clientDetailsToSave.patronymic;
    gender = clientDetailsToSave.gender;
    birthDate = clientDetailsToSave.birthdate;
    charm = charmStorage.get(clientDetailsToSave.charmId).toCharm();

    factualAddressInfo = new AddressInfo();
    factualAddressInfo.type = AddressType.FACTUAL;
    factualAddressInfo.flat = clientDetailsToSave.factualAddressInfo.flat;
    factualAddressInfo.house = clientDetailsToSave.factualAddressInfo.house;
    factualAddressInfo.street = clientDetailsToSave.factualAddressInfo.street;

    registrationAddressInfo = new AddressInfo();
    registrationAddressInfo.type = AddressType.REGISTRATION;
    registrationAddressInfo.flat = clientDetailsToSave.registrationAddressInfo.flat;
    registrationAddressInfo.house = clientDetailsToSave.registrationAddressInfo.house;
    registrationAddressInfo.street = clientDetailsToSave.registrationAddressInfo.street;

    if (phones == null)
      phones = new ArrayList<>();
    else {
      for (Phone deletedPhone : clientDetailsToSave.deletedPhones)
        phones.removeIf(curPhone -> curPhone.number.equals(deletedPhone.number) && curPhone.type == deletedPhone.type);
    }

    phones.addAll(clientDetailsToSave.phones);

    if (id != null) {
      this.id = id;
      generateAgeAndBalance(this);
    }
  }

  public static void generateAgeAndBalance(ClientDot clientDot) {
    Random random = new Random();

    clientDot.age = random.nextInt(40) + 18;
    clientDot.totalAccountBalance = "" + random.nextInt();
    clientDot.maxAccountBalance = "" + random.nextInt();
    clientDot.minAccountBalance = "" + random.nextInt();
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();

    ret.append("ClientDot of ").append(id).append("\n");
    ret.append("surname: ").append(surname).append("\n");
    ret.append("lastname: ").append(name).append("\n");
    ret.append("patronymic: ").append(patronymic).append("\n");
    ret.append("gender: ").append(gender.name()).append("\n");
    ret.append("birthdate: ").append(birthDate).append("\n");
    ret.append("charm: ").append(charm.id).append("\n");

    ret.append("residentialAddress: ").append(factualAddressInfo.street).append(" ");
    ret.append(factualAddressInfo.house).append(" ");
    ret.append(factualAddressInfo.flat).append("\n");

    ret.append("registrationAddress: ").append(registrationAddressInfo.street).append(" ");
    ret.append(registrationAddressInfo.house).append(" ");
    ret.append(registrationAddressInfo.flat).append("\n");

    for (Phone phone : phones) {
      ret.append("phone: ").append(phone.number).append(" ");
      ret.append(phone.type).append("\n");
    }

    return ret.toString();
  }
}
