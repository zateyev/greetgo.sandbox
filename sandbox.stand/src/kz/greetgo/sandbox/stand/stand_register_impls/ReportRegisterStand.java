package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ViewType;

import java.io.OutputStream;

@Bean
public class ReportRegisterStand implements ReportRegister {
  @Override
  public void genReport(String filterBy, String filterInput, String orderBy, boolean isDesc, ViewType viewType, OutputStream out) throws Exception {

  }
}
