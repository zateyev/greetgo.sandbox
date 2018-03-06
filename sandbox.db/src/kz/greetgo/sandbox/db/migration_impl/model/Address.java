package kz.greetgo.sandbox.db.migration_impl.model;

public class Address {
  public String cia_id;
  public String type;
  public String street;
  public String house;
  public String flat;

  public Address(String type) {
    this.type = type;
  }
}
