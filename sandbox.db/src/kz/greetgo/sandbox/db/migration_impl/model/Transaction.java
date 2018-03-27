package kz.greetgo.sandbox.db.migration_impl.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
  public String type;
  public BigDecimal money;
  public String finishedAt;
  public Date finishedAtD;
  public String transaction_type;
  public String account_number;
}
