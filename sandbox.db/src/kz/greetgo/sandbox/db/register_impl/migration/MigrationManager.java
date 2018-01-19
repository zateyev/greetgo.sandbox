package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.SshConnector.SSHManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Bean
public class MigrationManager {

  public BeanGetter<DbConfig> dbConfig;


  public Connection createConnection() throws SQLException {
    return DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password());
  }


  public void migrate() throws Exception {

    SSHManager sshManager = new SSHManager();
    sshManager.connectAndMigrateCia(createConnection());
    sshManager.connectAndMigrateFrs(createConnection());

  }

}
