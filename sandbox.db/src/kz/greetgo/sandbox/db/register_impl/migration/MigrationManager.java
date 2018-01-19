package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.SshConnector.SSHManager;

import java.sql.Connection;
import java.sql.DriverManager;

@Bean
public class MigrationManager {

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<SSHManager> sshManager;


  public Connection createConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password());
  }


  public void migrate() throws Exception {

    SSHManager sshManager = this.sshManager.get();
    try (Connection connection = createConnection()) {
      sshManager.connectAndMigrateCia(connection);
    }

    try (Connection connection = createConnection()) {
      sshManager.connectAndMigrateFrs(connection);
    }

  }

}
