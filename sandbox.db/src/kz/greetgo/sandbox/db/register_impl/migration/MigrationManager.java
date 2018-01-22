package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.SshConnector.SSHManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@Bean
public class MigrationManager {

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<SSHManager> sshManager;
  private final Logger logger = Logger.getLogger(getClass());


  public Connection createConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password());
  }


  public void migrate() throws Exception {
    logger.trace("[[[[[[ ------ MIGRATION STARTED ------ ]]]]]]]");
    logger.trace("[[[[[[ ------ " + getCurrentTimeStamp() + "");

    long started = System.nanoTime();

    SSHManager sshManager = this.sshManager.get();
    try (Connection connection = createConnection()) {
      sshManager.connectAndMigrateCia(connection);
    } catch (Exception e) {
      logger.trace("Exeption: " + e);
    }

    try (Connection connection = createConnection()) {
      sshManager.connectAndMigrateFrs(connection);
    } catch (Exception e) {
      logger.trace("Exeption: " + e);
    }


    logger.trace("[[[[[[ ------ MIGRATION ENDED ------ ]]]]]]]");
    logger.trace("[[[[[[ ------ " + getCurrentTimeStamp() + "");

    logger.trace("[[[[[[ ------ EXECUTION TIME ------ ]]]]]]]");
    logger.trace("[[[[[[ ------ "
      + TimeUnit.HOURS.convert(System.nanoTime() - started, TimeUnit.NANOSECONDS)
      + "  Hours  "
      + TimeUnit.MINUTES.convert(System.nanoTime() - started, TimeUnit.NANOSECONDS)
      + "  Minutes "
      + TimeUnit.SECONDS.convert(System.nanoTime() - started, TimeUnit.NANOSECONDS) +
      "    Seconds -------- ]]]]]]");

  }

  private String getCurrentTimeStamp() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
  }

}
