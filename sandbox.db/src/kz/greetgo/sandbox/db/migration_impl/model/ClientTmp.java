package kz.greetgo.sandbox.db.migration_impl.model;

import java.util.Date;

public class ClientTmp {
  public static final int STATUS_NOT_DUPLICATED = 2;
  public static final int STATUS_EXISTS = 3;

  public int number;
  public String cia_id;
  public String surname;
  public String name;
  public String patronymic;
  public String gender;
  public Date birth_date;
  public String charm_name;
  public String error;
  public int status;
  public boolean hasError;
}
