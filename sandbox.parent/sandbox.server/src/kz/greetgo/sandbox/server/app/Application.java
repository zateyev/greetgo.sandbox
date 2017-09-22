package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.Depinject;
import kz.greetgo.sandbox.db.util.App;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class Application implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    try {
      startUp(ctx);
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      if (e instanceof ServletException) throw (ServletException) e;
      throw new RuntimeException(e);
    }
  }

  private void startUp(ServletContext ctx) throws Exception {

    ApplicationBeanContainer abc = Depinject.newInstance(ApplicationBeanContainer.class);

    if (!App.do_not_run_liquibase_on_deploy_war().exists()) abc.getLiquibaseManager().apply();

    abc.getControllerServlet().register(ctx);
  }
}
