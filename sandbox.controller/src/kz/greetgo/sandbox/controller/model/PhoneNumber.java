package kz.greetgo.sandbox.controller.model;

public class PhoneNumber {
    private PhoneType phoneType;
    private String number;

    public PhoneNumber(PhoneType phoneType, String number) {
        this.phoneType = phoneType;
        this.number = number;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
