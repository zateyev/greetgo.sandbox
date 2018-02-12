package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.PhoneNumber;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ClientDot {
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
        ret.setId(id);
        ret.setSurname(surname);
        ret.setName(name);
        ret.setPatronymic(patronymic);
        ret.setCharm(charm);
        if (dateOfBirth != null)
            ret.setAge(Period.between(dateOfBirth, LocalDate.now()).getYears());
        ret.setTotalBalance(totalBalance);
        ret.setMinBalance(minBalance);
        ret.setMaxBalance(maxBalance);
        return ret;
    }

    public ClientDetails toClientsFullInfo() {
        ClientDetails ret = new ClientDetails();
        ret.setId(id);
        ret.setSurname(surname);
        ret.setName(name);
        ret.setPatronymic(patronymic);
        ret.setCharm(charm);
        ret.setGender(gender);
        ret.setDateOfBirth(dateOfBirth);

        ret.setAddressF(addressF);
        ret.setAddressR(addressR);

        ret.setPhoneNumbers(phoneNumbers);

        ret.setTotalBalance(totalBalance);
        ret.setMinBalance(minBalance);
        ret.setMaxBalance(maxBalance);
        return ret;
    }
}
