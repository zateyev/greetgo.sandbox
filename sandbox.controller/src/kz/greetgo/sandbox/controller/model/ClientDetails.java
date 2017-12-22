package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientPhones;

public class ClientDetails {
  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public String charmId;
  public String gender;
  public String dateOfBirth;
  public ClientAddress factAddress;
  public ClientAddress regAddress;
  public ClientPhones phones;
  public List<CharmRecord> charms = new ArrayList<>();
}
