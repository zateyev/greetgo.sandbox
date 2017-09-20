package kz.greetgo.sandbox.stand.stand_register_impls.model;

public class PersonDot {
  public String id;
  public String accountName;
  public boolean disabled = false;
  public String surname, name, patronymic;

  public String fio() {
    return surname + ' ' + name + (patronymic == null ? "" : " " + patronymic);
  }

}
