package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;
import kz.greetgo.sandbox.controller.model.AddressInfo;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.Phone;

public class ClientDetails {
  public Long id;
  public String surname;
  public String name;
  public String patronymic;
  public Gender gender;
  public String birthdate;
  public int charmId;
  public List<Charm> charmList = new ArrayList<>();
  public AddressInfo registrationAddressInfo;
  public AddressInfo factualAddressInfo;
  public List<Phone> phones = new ArrayList<>();
}
