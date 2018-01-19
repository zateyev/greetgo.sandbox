package kz.greetgo.sandbox.db.register_impl.migration.SshConnector;

import com.jcraft.jsch.UserInfo;

public class SshUserInfo implements UserInfo {

  private String password;

  @Override
  public String getPassphrase() {
    return null;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean promptPassword(String message) {
    this.password = message;
    return true;
  }

  @Override
  public boolean promptPassphrase(String message) {
    return true;
  }

  @Override
  public boolean promptYesNo(String message) {
    return true;
  }

  @Override
  public void showMessage(String message) {

  }
}
