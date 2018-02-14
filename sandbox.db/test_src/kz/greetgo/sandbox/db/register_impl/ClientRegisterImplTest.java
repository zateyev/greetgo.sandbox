package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGen;

  @DataProvider
  public Object[][] saveClient_DP() {
    List<Object[]> list = Arrays.stream(UserParamName.values())
      .map(a -> new Object[]{a})
      .collect(Collectors.toList());

    return list.toArray(new Object[list.size()][]);
  }

  @Test(dataProvider = "saveClient_DP")
  public void getParam_value(UserParamName paramName) throws Exception {

    String personId = RND.str(10);
    String expectedValue = RND.str(10);

    clientTestDao.get().insertClient(idGen.get().newId(), "", "", "", null, null, 1);

    //
    //
    long size = clientRegister.get().getTotalSize("", "");
    //
    //

    assertThat(size).isEqualTo(1);
  }
}
