package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
  public List<Phone> phones;

  public int age;
  public long totalAccountBalance;
  public long maxAccountBalance;
  public long minAccountBalance;

  public ClientDetails toClientDetails() {
    ClientDetails ret = new ClientDetails();

    ret.id = id;
    ret.surname = surname;
    ret.lastname = lastname;
    ret.patronymic = patronymic;
    ret.gender = gender;
    ret.birthdate = birthDate;
    ret.charmId = charm.id;

    ret.residentialAddressInfo = new ResidentialAddressInfo();
    ret.residentialAddressInfo.flat = residentialAddressInfo.flat;
    ret.residentialAddressInfo.home = residentialAddressInfo.home;
    ret.residentialAddressInfo.street = residentialAddressInfo.street;

    ret.registrationAddressInfo = new RegistrationAddressInfo();
    ret.registrationAddressInfo.flat = registrationAddressInfo.flat;
    ret.registrationAddressInfo.home = registrationAddressInfo.home;
    ret.registrationAddressInfo.street = registrationAddressInfo.street;

    ret.phones.addAll(phones);

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

    return ret;
  }

  public void toClientDot(ClientDetailsToSave clientDetailsToSave, Long id, Map<String, CharmDot> charmStorage) {
    surname = clientDetailsToSave.surname;
    lastname = clientDetailsToSave.lastname;
    patronymic = clientDetailsToSave.patronymic;
    gender = clientDetailsToSave.gender;
    birthDate = clientDetailsToSave.birthdate;
    charm = charmStorage.get(clientDetailsToSave.charmId).toCharm();

    residentialAddressInfo = new ResidentialAddressInfo();
    residentialAddressInfo.flat = clientDetailsToSave.residentialAddressInfo.flat;
    residentialAddressInfo.home = clientDetailsToSave.residentialAddressInfo.home;
    residentialAddressInfo.street = clientDetailsToSave.residentialAddressInfo.street;

    registrationAddressInfo = new RegistrationAddressInfo();
    registrationAddressInfo.flat = clientDetailsToSave.registrationAddressInfo.flat;
    registrationAddressInfo.home = clientDetailsToSave.registrationAddressInfo.home;
    registrationAddressInfo.street = clientDetailsToSave.registrationAddressInfo.street;

    phones = new ArrayList<>();
    phones.addAll(clientDetailsToSave.phones);

    if (id != null) {
      this.id = id;
      generateAgeAndBalance(this);
    }
  }

  public static void generateAgeAndBalance(ClientDot clientDot) {
    Random random = new Random();

    clientDot.age = random.nextInt(40) + 18;
    clientDot.totalAccountBalance = random.nextInt();
    clientDot.maxAccountBalance = random.nextInt();
    clientDot.minAccountBalance = random.nextInt();
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

    for (Phone phone : phones) {
      ret.append("phone: ").append(phone.number).append(" ");
      ret.append(phone.type).append("\n");
    }

    return ret.toString();
  }
}
