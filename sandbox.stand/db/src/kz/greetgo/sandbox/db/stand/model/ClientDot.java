package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ClientDot {
  private String id;
  private String surname;
  private String name;
  private String patronymic;
  private Charm charm;
  private Gender gender;
  private LocalDate dateOfBirth;
  private Address addressF;
  private Address addressR;
  private List<PhoneNumber> phoneNumbers;
  private int totalBalance;
  private int minBalance;
  private int maxBalance;

  public ClientDot(String id, String surname, String name, String patronymic, Address addressF, Address addressR, List<PhoneNumber> phoneNumbers) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.addressF = addressF;
    this.addressR = addressR;
    this.phoneNumbers = phoneNumbers;
  }

  public ClientDot() {

  }

  public ClientDot(ClientRecords clientRecords) {
    saveRecords(clientRecords);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPatronymic() {
    return patronymic;
  }

  public void setPatronymic(String patronymic) {
    this.patronymic = patronymic;
  }

  public Charm getCharm() {
    return charm;
  }

  public void setCharm(Charm charm) {
    this.charm = charm;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Address getAddressF() {
    return addressF;
  }

  public void setAddressF(Address addressF) {
    this.addressF = addressF;
  }

  public Address getAddressR() {
    return addressR;
  }

  public void setAddressR(Address addressR) {
    this.addressR = addressR;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public int getTotalBalance() {
    return totalBalance;
  }

  public void setTotalBalance(int totalBalance) {
    this.totalBalance = totalBalance;
  }

  public int getMinBalance() {
    return minBalance;
  }

  public void setMinBalance(int minBalance) {
    this.minBalance = minBalance;
  }

  public int getMaxBalance() {
    return maxBalance;
  }

  public void setMaxBalance(int maxBalance) {
    this.maxBalance = maxBalance;
  }

  public ClientInfo toClientInfo() {
    ClientInfo ret = new ClientInfo();
    ret.id = id;
    ret.surname = surname;
    ret.name = name;
    ret.patronymic = patronymic;
    ret.charm = charm;
    if (dateOfBirth != null)
      ret.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
    ret.totalBalance = totalBalance;
    ret.minBalance = minBalance;
    ret.maxBalance = maxBalance;
    return ret;
  }

  public ClientDetails toClientDetails() {
    ClientDetails ret = new ClientDetails();
    ret.id = id;
    ret.surname = surname;
    ret.name = name;
    ret.patronymic = patronymic;
    ret.charm = charm;
    ret.gender = gender;
    if (dateOfBirth != null) ret.dateOfBirth = dateOfBirth.toString();
    ret.addressF = addressF;
    ret.addressR = addressR;
    ret.phoneNumbers = phoneNumbers;
    ret.totalBalance = totalBalance;
    ret.minBalance = minBalance;
    ret.maxBalance = maxBalance;
    return ret;
  }

  public void saveRecords(ClientRecords clientRecords) {
    this.id = clientRecords.id;
    this.surname = clientRecords.surname != null ? clientRecords.surname : "";
    this.name = clientRecords.name != null ? clientRecords.name : "";
    this.patronymic = clientRecords.patronymic != null ? clientRecords.patronymic : "";
    if (clientRecords.charm != null) {
      this.charm = clientRecords.charm;
    } else {
      this.charm = new Charm();
    }
    this.gender = clientRecords.gender;
    System.out.println(clientRecords.dateOfBirth);
    if (clientRecords.dateOfBirth != null && !clientRecords.dateOfBirth.isEmpty()) {
      this.dateOfBirth = LocalDate.parse(clientRecords.dateOfBirth);
    }
    this.addressF = clientRecords.addressF;
    this.addressR = clientRecords.addressR;
    this.phoneNumbers = clientRecords.phoneNumbers;
    this.totalBalance = clientRecords.totalBalance;
    this.minBalance = clientRecords.minBalance;
    this.maxBalance = clientRecords.maxBalance;
  }
}
