package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneInfo;
import kz.greetgo.sandbox.controller.model.RegistrationAddressInfo;
import kz.greetgo.sandbox.controller.model.ResidentialAddressInfo;

public class ClientDetails {
  public long id;
  public String surname;
  public String lastname;
  public String patronymic;
  public Gender gender;
  public String birthDate;
  public Charm charm;
  public RegistrationAddressInfo registrationAddressInfo;
  public ResidentialAddressInfo residentialAddressInfo;
  public PhoneInfo phoneInfo;
}
