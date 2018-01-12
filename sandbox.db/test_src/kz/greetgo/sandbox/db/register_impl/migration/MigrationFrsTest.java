package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.*;

public class MigrationFrsTest extends ParentTestNg{

  public BeanGetter<MigrationManager> migrationManager;

  Connection connection;

  @BeforeMethod
  public void createConnection() throws Exception {
    connection = migrationManager.get().createConnection();
  }

  @AfterMethod
  public void closeConnection() throws Exception {
    connection.close();
    connection = null;
  }

  @Test
  public void createTempTables() throws SQLException {

    MigrationFrs migration = new MigrationFrs();
    migration.connection = connection;

    migration.createTempTables();

  }
}