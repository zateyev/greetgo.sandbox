package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Charm;

public class CharmDot {
  //TODO: long?
  public String id;
  public String name;
  //TODO: disabled семантически лучше, т. к. по стандарту false
  public boolean isDisabled;

  public Charm toCharm() {
    Charm ret = new Charm();

    ret.id = id;
    ret.name = name;

    return ret;
  }
}
