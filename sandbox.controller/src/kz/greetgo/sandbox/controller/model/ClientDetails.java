package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientDetails {
  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public String charm;
  public String gender;
  public String dateOfBirth;
  public List<String> firstAddress = new ArrayList<>();
  public List<String> secondAddress = new ArrayList<>();
  public List<String> phones = new ArrayList<>();
  public List<String> charms = new ArrayList<>();
}
