package kz.greetgo.sandbox.controller.errors;

public class InvalidParameter extends RestError {
  public InvalidParameter() {
    super(400, "Неверный параметр для запроса");
  }
}
