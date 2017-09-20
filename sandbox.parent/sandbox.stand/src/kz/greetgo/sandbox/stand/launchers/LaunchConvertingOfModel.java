package kz.greetgo.sandbox.stand.launchers;

import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.stand.modelling.ConvertModel;

import java.io.File;

public class LaunchConvertingOfModel {
  public static void main(String[] args) throws Exception {
    File sourceDir = Modules.clientDir().toPath()
      .resolve("front/ts/model").toFile();
    File destinationDir = Modules.controllerDir().toPath()
      .resolve("src").toFile();
    String destinationPackage = "kz.greetgo.sandbox.controller.model";
    new ConvertModel(sourceDir, destinationDir, destinationPackage)
      .exec();
  }
}
