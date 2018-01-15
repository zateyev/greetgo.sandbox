package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.CharmType;
import kz.greetgo.sandbox.controller.model.GenderType;
import kz.greetgo.sandbox.controller.model.PhoneInfo;
import kz.greetgo.sandbox.controller.model.RegistrationAddressInfo;
import kz.greetgo.sandbox.controller.model.ResidentialAddressInfo;

public class ClientInfo {
  public long id;
  public String surname;
  public String lastname;
  public String patronymic;
  public GenderType gender;
  public String birthDate;
  public CharmType charm;
  public RegistrationAddressInfo registrationAddressInfo;
  public ResidentialAddressInfo residentialAddressInfo;
  public PhoneInfo phoneInfo;
}
