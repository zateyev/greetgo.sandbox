package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class Client {
    public String id;
    public String accountName;
    public String surname;
    public String name;
    public String patronymic;
    public List<PhoneNumber> phoneNumbers;
    public String charm;
    public Date dateOfBirth;
    public int totalBalance;
    public int minBalance;
    public int maxBalance;
}
