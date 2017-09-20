package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import org.apache.commons.lang.NotImplementedException;

@Bean
public class AuthRegisterStand implements AuthRegister {
  @Override
  public UserInfo auth(String username, String password) throws Exception {
    throw new NotImplementedException();
  }
}
