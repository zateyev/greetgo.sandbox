package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.model.RegistrationAddressInfo;
import kz.greetgo.sandbox.controller.model.ResidentialAddressInfo;

public class ClientDetailsToSave {
  public Long id;
  public String surname;
  public String lastname;
  public String patronymic;
  public Gender gender;
  public String birthdate;
  public int charmId;
  public RegistrationAddressInfo registrationAddressInfo;
  public ResidentialAddressInfo residentialAddressInfo;
  public List<Phone> phones = new ArrayList<>();
}
