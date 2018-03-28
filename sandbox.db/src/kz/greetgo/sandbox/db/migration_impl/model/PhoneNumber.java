package kz.greetgo.sandbox.db.migration_impl.model;

public class PhoneNumber {
  public static final int STATUS_DUPLICATED_PHONE_NUMBER = 2;

  public int number;
  public int client_num;
  public PhoneType type;
  public String phone_number;
  public int status;
}
