package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры миграции")
public interface MigrationConfig {

  @Description("Максимальный размер батча")
  @DefaultIntValue(50_000)
  int maxBatchSize();

  @Description("Имя файла с ошибками")
  @DefaultStrValue("errors.txt")
  String outErrorFileName();

  @Description("Имя ssh сервера")
  String sshUser();

  @Description("Пароль ssh сервера")
  String sshPassword();

  @Description("IP адрес ssh сервера")
  String sshHost();

  @Description("Номер порта ssh сервера")
  @DefaultIntValue(22)
  int sshPort();

  @Description("Путь к директории где лежат файлы")
  @DefaultStrValue("/home/zateyev/git/greetgo.sandbox/build/out_files/")
  String inFilesHomePath();

  @DefaultStrValue("build/report/")
  String sqlReportDir();
}
