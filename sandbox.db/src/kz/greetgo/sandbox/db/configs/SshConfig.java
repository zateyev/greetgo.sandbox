package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа по SSH")
public interface SshConfig {

  @Description("Порт доступа по SSH")
  @DefaultIntValue(22)
  int port();

  @Description("Время переподключения")
  @DefaultIntValue(10000)
  int timeout();

  @Description("Имя хоста")
  @DefaultStrValue("192.168.11.85")
  String hostName();

  @Description("Имя пользователя")
  @DefaultStrValue("mkasyanov")
  String username();

  @Description("Пароль")
  @DefaultStrValue("111")
  String pass();

  @Description("Директория на сервере")
  @DefaultStrValue("canada/")
  String serverDir();


  @Description("Локальная директория")
  @DefaultStrValue("build/migration/")
  String localDir();

}
