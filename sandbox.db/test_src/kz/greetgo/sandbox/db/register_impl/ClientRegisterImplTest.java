package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGen;

  @Test
  public void getTotalSize_noFilter() {

    clientTestDao.get().removeAllData();

    List<ClientDot> clientDots = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      ClientDot clientDot = randClientDot();
      clientTestDao.get().insertClientDot(clientDot.getId(), clientDot.getSurname(), clientDot.getName(),
        clientDot.getPatronymic(), clientDot.getGender(), Date.valueOf(clientDot.getDateOfBirth()), clientDot.getCharm().name);
      clientDots.add(clientDot);
    }

    //
    //
    long result = clientRegister.get().getTotalSize("name", "");
    //
    //

    assertThat(result).isEqualTo(clientDots.size());
  }

  private ClientDot randClientDot() {
    ClientDot clientDot = new ClientDot();
    clientDot.setId(idGen.get().newId());
    clientDot.setSurname(RND.str(10));
    clientDot.setName(RND.str(10));
    clientDot.setPatronymic(RND.str(10));
    Charm charm = new Charm();
    charm.name = RND.str(10);
    clientDot.setCharm(charm);
    clientDot.setGender(RND.someEnum(Gender.values()));
    clientDot.setDateOfBirth(LocalDate.now());
    return clientDot;
  }
}
