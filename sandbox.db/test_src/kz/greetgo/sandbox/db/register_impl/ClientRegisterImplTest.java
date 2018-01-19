package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

public class ClientRegisterImplTest extends ParentTestNg {
  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  private void generateAndInsertClient() {


    //clientTestDao.get().insertClient();
  }

  @Test
  public void getPageCount_ok() {
    clientTestDao.get().clearTableClient();



  }
}
