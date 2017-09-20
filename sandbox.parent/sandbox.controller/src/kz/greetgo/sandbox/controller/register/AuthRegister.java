package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.UserInfo;

public interface AuthRegister {
  UserInfo auth(String username, String password) throws Exception;
}
