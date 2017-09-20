package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.annotations.Mapping;

@Bean
@Mapping("/auth")
public class AuthController {

  @Mapping("/login")
  public void login() {

  }
}
