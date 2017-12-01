package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.AuthRegister;
/**
 * Created by jgolibzhan on 11/30/17.
 */
@Bean
@Mapping("/client")
public class ClientController implements Controller{
  public BeanGetter<AuthRegister> authRegister;

  @ToJson
  @Mapping("/getList")
  public ClientRecord[] getList(){
    return authRegister.get().getList();
      }

  @ToJson
  @Mapping("/getNum")
  public String getnum(){
    return "2";
  }
}
