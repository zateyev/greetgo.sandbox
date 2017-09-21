package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.NoImplementor;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Bean
public class AuthRegisterImpl implements AuthRegister {
  @Override
  public void saveParam(String personId, UserParamName name, String value) {
    throw new NotImplementedException();
  }

  @Override
  public String getParam(String personId, UserParamName name) {
    throw new NotImplementedException();
  }

  @Override
  public String login(String accountName, String password) {
    throw new NotImplementedException();
  }

  @Override
  public void checkTokenAndPutToThreadLocal(String token) {
    throw new NotImplementedException();
  }

  @Override
  public void cleanTokenThreadLocal() {
    throw new NotImplementedException();
  }

  @Override
  public SessionInfo getSessionInfo() {
    throw new NotImplementedException();
  }

  @Override
  public AuthInfo getAuthInfo(String personId) {
    throw new NotImplementedException();
  }

  @Override
  public UserInfo getUserInfo(String personId) {
    throw new NotImplementedException();
  }
}
