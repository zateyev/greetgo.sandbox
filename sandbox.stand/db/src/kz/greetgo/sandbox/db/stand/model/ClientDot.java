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
  private double totalBalance;
  private double minBalance;
  private double maxBalance;

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

  public ClientDot(ClientRecordsToSave clientRecordsToSave) {
    saveRecords(clientRecordsToSave);
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

  public double getTotalBalance() {
    return totalBalance;
  }

  public void setTotalBalance(double totalBalance) {
    this.totalBalance = totalBalance;
  }

  public double getMinBalance() {
    return minBalance;
  }

  public void setMinBalance(double minBalance) {
    this.minBalance = minBalance;
  }

  public double getMaxBalance() {
    return maxBalance;
  }

  public void setMaxBalance(double maxBalance) {
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

  public void saveRecords(ClientRecordsToSave clientRecordsToSave) {
    this.id = clientRecordsToSave.id;
    this.surname = clientRecordsToSave.surname != null ? clientRecordsToSave.surname : "";
    this.name = clientRecordsToSave.name != null ? clientRecordsToSave.name : "";
    this.patronymic = clientRecordsToSave.patronymic != null ? clientRecordsToSave.patronymic : "";
    if (clientRecordsToSave.charm != null) {
      this.charm = clientRecordsToSave.charm;
    } else {
      this.charm = new Charm();
    }
    this.gender = clientRecordsToSave.gender;
    System.out.println(clientRecordsToSave.dateOfBirth);
    if (clientRecordsToSave.dateOfBirth != null && !clientRecordsToSave.dateOfBirth.isEmpty()) {
      this.dateOfBirth = LocalDate.parse(clientRecordsToSave.dateOfBirth);
    }
    this.addressF = clientRecordsToSave.addressF;
    this.addressR = clientRecordsToSave.addressR;
    this.phoneNumbers = clientRecordsToSave.phoneNumbers;
    this.totalBalance = clientRecordsToSave.totalBalance;
    this.minBalance = clientRecordsToSave.minBalance;
    this.maxBalance = clientRecordsToSave.maxBalance;
  }
}
