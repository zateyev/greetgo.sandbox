package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void getClient_CREATE() throws Exception {

    clientTestDao.get().deleteAllCharms();

    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));

    //
    //
    ClientDetails details = clientRegister.get().getClient(null);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.id).isNull();
    assertThat(details.charmId).isNull();
    assertThat(details.charms).isNotNull();
    assertThat(details.charms).hasSize(2);
  }

  @Test
  public void getClient_UPDATE() throws Exception {

    clientTestDao.get().deleteAllCharms();

    String charmId1 = RND.str(10);
    String charmId2 = RND.str(10);

    clientTestDao.get().insertCharm(charmId1, RND.str(10));
    clientTestDao.get().insertCharm(charmId2, RND.str(10));

    String clientId = RND.str(10);

    String surname = RND.str(10);
    String name = RND.str(10);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "charm_id", charmId1);
    clientTestDao.get().update(clientId, "surname", surname);
    clientTestDao.get().update(clientId, "name", name);

    //
    //
    ClientDetails details = clientRegister.get().getClient(null);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.id).isEqualTo(clientId);
    assertThat(details.surname).isEqualTo(surname);
    assertThat(details.name).isEqualTo(name);
    assertThat(details.charmId).isEqualTo(charmId1);
    assertThat(details.charms).isNotNull();

    List<String> charmList = new ArrayList<>();
    charmList.add(charmId1);
    charmList.add(charmId2);
    charmList.sort(Comparator.comparing(s -> s));

    assertThat(details.charms).hasSameSizeAs(charmList);
    assertThat(details.charms.get(0)).isEqualTo(charmList.get(0));
    assertThat(details.charms.get(1)).isEqualTo(charmList.get(1));
  }
}