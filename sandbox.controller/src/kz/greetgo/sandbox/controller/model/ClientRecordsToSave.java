package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientRecordsToSave {
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
  public double totalBalance;/**/
  public double minBalance;/**/
  public double maxBalance;/**/

  public String toXml() {
    StringBuilder sb = new StringBuilder();
    sb.append("<client id=\"").append(id).append("\">\n");
    if (surname != null) sb.append("  <surname value=\"").append(surname).append("\"/>\n");
    if (name != null) sb.append("  <name value=\"").append(name).append("\"/>\n");
    if (patronymic != null) sb.append("  <patronymic value=\"").append(patronymic).append("\"/>\n");
    if (dateOfBirth != null) sb.append("  <birth value=\"").append(dateOfBirth).append("\"/>\n");
    sb.append("  <address>\n");
    if (addressF != null) {
      sb.append("    <fact street=\"").append(addressF.street)
        .append("\" house=\"").append(addressF.house)
        .append("\" flat=\"").append(addressF.flat).append("\"/>\n");
    }
    if (addressR != null) {
      sb.append("    <register street=\"").append(addressR.street)
        .append("\" house=\"").append(addressR.house)
        .append("\" flat=\"").append(addressR.flat).append("\"/>\n");
    }
    sb.append("  </address>\n");
    if (phoneNumbers != null) {
      for (PhoneNumber phoneNumber : phoneNumbers) {
        switch (phoneNumber.phoneType) {
          case HOME:
            sb.append("  <homePhone>").append(phoneNumber.number).append("</homePhone>\n");
            break;
          case WORK:
            sb.append("  <workPhone>").append(phoneNumber.number).append("</workPhone>\n");
            break;
          case MOBILE:
            sb.append("  <mobilePhone>").append(phoneNumber.number).append("</mobilePhone>\n");
        }
      }
    }
    sb.append("</client>\n");
    return sb.toString();
  }

  public static void main(String[] args) {
    ClientRecordsToSave clientRecords = new ClientRecordsToSave();
    clientRecords.id = "asd";
    clientRecords.surname = "asd";
    clientRecords.name = "asd";
    clientRecords.addressF = new Address();
    clientRecords.addressF.street = "street";
    clientRecords.addressF.house = "B";
    clientRecords.addressF.flat = "23";
    clientRecords.addressR = new Address();
    clientRecords.addressR.street = "street reg";
    clientRecords.addressR.house = "regH";
    clientRecords.addressR.flat = "23r";
    clientRecords.phoneNumbers = new ArrayList<>();
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.phoneType = PhoneType.HOME;
    phoneNumber.number = "7074129845";
    PhoneNumber phoneNumber2 = new PhoneNumber();
    phoneNumber2.phoneType = PhoneType.MOBILE;
    phoneNumber2.number = "7074129845";
    PhoneNumber phoneNumber3 = new PhoneNumber();
    phoneNumber3.phoneType = PhoneType.WORK;
    phoneNumber3.number = "7273357848 вн. 2115";
    clientRecords.phoneNumbers.add(phoneNumber);
    clientRecords.phoneNumbers.add(phoneNumber2);
    clientRecords.phoneNumbers.add(phoneNumber3);
    System.out.println(clientRecords.toXml());
  }
}
