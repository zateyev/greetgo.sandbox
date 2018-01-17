package kz.greetgo.sandbox.db.register_impl.migration.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
  public String type, client_id, account_number, registered_at;
}
