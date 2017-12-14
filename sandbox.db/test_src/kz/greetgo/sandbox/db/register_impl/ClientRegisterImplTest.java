package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void getClient_CREATE() throws Exception {

    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));
    clientTestDao.get().insertCharm(RND.str(10), RND.str(10));

    //
    //
    ClientDetails details = clientRegister.get().getClient("");
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

    String charmId1 = RND.str(10), charmName1 = "B" + RND.str(10);
    String charmId2 = RND.str(10), charmName2 = "A" + RND.str(10);

    clientTestDao.get().insertCharm(charmId1, charmName1);
    clientTestDao.get().insertCharm(charmId2, charmName2);

    String clientId = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "charm_id", charmId1);
    clientTestDao.get().update(clientId, "surname", surname);
    clientTestDao.get().update(clientId, "name", name);

    //
    //
    ClientDetails details = clientRegister.get().getClient(clientId);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.id).isEqualTo(clientId);
    assertThat(details.surname).isEqualTo(surname);
    assertThat(details.name).isEqualTo(name);
    assertThat(details.charmId).isEqualTo(charmId1);
    assertThat(details.charms).isNotNull();

    assertThat(details.charms).hasSize(2);
    assertThat(details.charms.get(0).name).isEqualTo(charmName2);
    assertThat(details.charms.get(1).name).isEqualTo(charmName1);
  }


  @Test
  public void deleteClient_NOTNULLid() throws Exception {

    String clientId = RND.str(5);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    String clientId2 = RND.str(5);

    clientTestDao.get().insert(clientId2);
    clientTestDao.get().update(clientId2, "name", RND.str(10));
    clientTestDao.get().update(clientId2, "surname", RND.str(10));
    clientTestDao.get().update(clientId2, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId2, "actual", 1);
    //
    //
    clientRegister.get().deleteClient(clientId);
    //
    //
    assertThat(clientTestDao.get().getActualClient(clientId)).isEqualTo("0");
    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo("1");

  }

  @Test
  public void deleteClient_NULLid() {

    String clientId = RND.str(5);

    clientTestDao.get().insert(clientId);
    clientTestDao.get().update(clientId, "name", RND.str(10));
    clientTestDao.get().update(clientId, "surname", RND.str(10));
    clientTestDao.get().update(clientId, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId, "actual", 1);

    String clientId2 = RND.str(5);

    clientTestDao.get().insert(clientId2);
    clientTestDao.get().update(clientId2, "name", RND.str(10));
    clientTestDao.get().update(clientId2, "surname", RND.str(10));
    clientTestDao.get().update(clientId2, "patronymic", RND.str(10));
    clientTestDao.get().update(clientId2, "actual", 1);

    //
    //
    clientRegister.get().deleteClient(null);
    //
    //

    assertThat(clientTestDao.get().getActualClient(clientId)).isEqualTo("1");
    assertThat(clientTestDao.get().getActualClient(clientId2)).isEqualTo("1");

  }

  @Test
  public void saveClient_NULLid() {

    ClientToSave cl = new ClientToSave();

    String charmId = RND.str(10);
    String charmName = RND.str(10);


    clientTestDao.get().insertCharm(charmId, charmName);

    cl.id = null;
    cl.name = RND.str(10);
    cl.surname = RND.str(10);
    cl.patronymic = RND.str(10);
    cl.charmId = charmId;
    //
    //
    ClientRecord rec = clientRegister.get().saveClient(cl);
    //
    //
    assertThat(clientTestDao.get().loadDetails(rec.id)).isNotNull();
    assertThat(clientTestDao.get().loadDetails(rec.id).name).isEqualTo(cl.name);
    assertThat(clientTestDao.get().loadDetails(rec.id).surname).isEqualTo(cl.surname);
    assertThat(clientTestDao.get().loadDetails(rec.id).patronymic).isEqualTo(cl.patronymic);
    assertThat(clientTestDao.get().loadDetails(rec.id).charmId).isEqualTo(cl.charmId);

  }

}