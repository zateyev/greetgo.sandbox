package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.core.BeanContainer;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.db.util.LiquibaseManager;
import kz.greetgo.sandbox.server.beans.ControllerServlet;

@Include(BeanConfigApplication.class)
public interface ApplicationBeanContainer extends BeanContainer {
  LiquibaseManager getLiquibaseManager();

  ControllerServlet getControllerServlet();
}
