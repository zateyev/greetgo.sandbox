package kz.greetgo.sandbox.controller.model;

public class ClientInfo {
    private String id;
    private String surname;
    private String name;
    private String patronymic;
    private String charm;
    private int age;
    private int totalBalance;
    private int minBalance;
    private int maxBalance;

    public ClientInfo(String id,
                    String surname,
                    String name,
                    String patronymic,
                    String charm,
                    int age,
                    int totalBalance,
                    int minBalance,
                    int maxBalance) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.charm = charm;
        this.age = age;
        this.totalBalance = totalBalance;
        this.minBalance = minBalance;
        this.maxBalance = maxBalance;
    }

    public ClientInfo() {

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
}
