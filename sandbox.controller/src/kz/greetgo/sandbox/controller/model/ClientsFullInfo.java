package kz.greetgo.sandbox.controller.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ClientsFullInfo {
    private String id;
    private String surname;
    private String name;
    private String patronymic;
    private String charm;
    private String gender;
    private LocalDate dateOfBirth;
    private Address addressF;
    private Address addressR;
    private List<PhoneNumber> phoneNumbers;
    private int totalBalance;
    private int minBalance;
    private int maxBalance;

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

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return Objects.toString(dateOfBirth, "");
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

    public void setAddressF(String street, String building, String apartment) {
        this.addressF = new Address(street, building, apartment);
    }

    public void setAddressR(String street, String building, String apartment) {
        this.addressR = new Address(street, building, apartment);
    }
}
