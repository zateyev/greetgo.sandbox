package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.ClientInfo;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.*;

public class ReportRegisterImplTest {
  @Test
  public void genReport() throws Exception {
    ReportRegisterImpl register = new ReportRegisterImpl();

    final ClientInfo inData[] = new ClientInfo[1];

    // clean and add data to DB

    //
    //
//    register.genReport("asdUserId", "asdContractId", clientInfo -> inData[0] = clientInfo, out);
    //
    //

    assertThat(inData[0].surname).isNotNull();
  }
}