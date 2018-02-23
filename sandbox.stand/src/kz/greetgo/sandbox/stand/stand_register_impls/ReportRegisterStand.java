package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ViewType;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Bean
public class ReportRegisterStand implements ReportRegister {
  @Override
  public void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, ViewType viewType, OutputStream out) throws Exception {
//    File file = new File("build/report/report." + viewType.name().toLowerCase());
//    Path path = file.toPath();
//    Files.copy(path, out);
  }
}
