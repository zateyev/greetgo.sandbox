package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.Collator;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link CharmRegisterImpl}
 */
public class CharmRegisterImplTest extends ParentTestNg {

  public BeanGetter<CharmRegister> charmRegister;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGen;

  @Test
  public void getCharms_ok() {
    charmTestDao.get().removeAllData();

    List<Charm> charms = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Charm charm = createRndCharm();
      charmTestDao.get().insertCharm(charm.id, charm.name, charm.description, charm.energy);
      charms.add(charm);
    }

    charms.sort(Comparator.comparing(charm -> charm.name.toLowerCase()));

    List<String> charmNames = new ArrayList<>();
    charms.forEach(charm -> charmNames.add(charm.name));

    //
    //
    List<String> result = charmRegister.get().getCharms();
    //
    //

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(charmNames.size());
    for (int i = 0; i < charmNames.size(); i++) {
      assertThat(result.get(i)).isEqualTo(charmNames.get(i));
    }
  }

  @Test
  public void getCharms_empty() {
    charmTestDao.get().removeAllData();

    //
    //
    List<String> result = charmRegister.get().getCharms();
    //
    //

    assertThat(result).isEmpty();
  }

  private Charm createRndCharm() {
    Charm charm = new Charm();
    charm.id = idGen.get().newId();
    charm.name = RND.str(10);
    charm.description = RND.str(10);
    charm.energy = RND.plusDouble(100, 2);
    return charm;
  }
}
