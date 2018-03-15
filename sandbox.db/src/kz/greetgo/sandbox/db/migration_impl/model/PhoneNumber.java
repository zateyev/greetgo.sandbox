package kz.greetgo.sandbox.db.migration_impl.model;

public class PhoneNumber {
  public int client_num;
  public String type;
  public String number;

  public PhoneNumber(String type) {
    this.type = type;
  }
}
