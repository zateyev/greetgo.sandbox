package kz.greetgo.sandbox.controller.errors;

public class IllegalLoginOrPassword extends RestError {
  public IllegalLoginOrPassword() {
    super(401, "Illegal login or password");
  }
}
