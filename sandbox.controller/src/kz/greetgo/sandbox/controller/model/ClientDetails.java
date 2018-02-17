package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientDetails {
  public String id;
  public String surname;
  public String name;
  public String patronymic;
  public Charm charm;
  public Gender gender;
  public String dateOfBirth;
  public Address addressF;
  public Address addressR;
  public List<PhoneNumber> phoneNumbers = new ArrayList<>();
  public double totalBalance;
  public double minBalance;
  public double maxBalance;
}
