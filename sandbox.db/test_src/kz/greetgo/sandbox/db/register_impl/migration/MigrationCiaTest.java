package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import kz.greetgo.util.ServerUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationCiaTest extends ParentTestNg {

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
  public void createTempTables() throws Exception {
    MigrationCia migration = new MigrationCia();
    migration.connection = connection;

    migration.createTempTables();
  }

  @Test
  public void uploadFileToTempTables() throws Exception {

    MigrationCia migration = new MigrationCia();
    migration.connection = connection;
    migration.inFile = createInFile("cia_test_1.xml");

    migration.createTempTables();
    migration.uploadFileToTempTables();

    Map<Object, Map<String, Object>> client = loadTable("no", migration.clientTable);
    Map<Object, Map<String, Object>> address = loadTable("client", migration.addressTable);

    assertThat(client).hasSize(2);
    assertThat(address).hasSize(2);

    assertThat(client.get(1L).get("id")).isEqualTo("4-DU8-32-H7");
    assertThat(client.get(1L).get("surname")).isEqualTo("Иванов");
    assertThat(client.get(1L).get("name")).isEqualTo("Иван");
    assertThat(client.get(1L).get("patronymic")).isEqualTo("Иваныч");
    assertThat(client.get(1L).get("gender")).isEqualTo("MALE");
    assertThat(client.get(1L).get("charm")).isEqualTo("Уситчивый");
    assertThat(client.get(1L).get("birth")).isEqualTo("1980-11-12");


    assertThat(address.get(1L).get("type")).isEqualTo("fact");
    assertThat(address.get(1L).get("street")).isEqualTo("Панфилова");
    assertThat(address.get(1L).get("house")).isEqualTo("23A");
    assertThat(address.get(1L).get("flat")).isEqualTo("22");

    assertThat(address.get(1L).get("type")).isEqualTo("reg");
    assertThat(address.get(1L).get("street")).isEqualTo("Абая");
    assertThat(address.get(1L).get("house")).isEqualTo("24A");
    assertThat(address.get(1L).get("flat")).isEqualTo("2");


  }


  private File createInFile(String resourceName) throws Exception {
    File ret = new File("build/inFile_" + RND.intStr(10) + "_" + resourceName);
    ret.getParentFile().mkdirs();
    try (InputStream in = getClass().getResourceAsStream(resourceName)) {
      try (FileOutputStream out = new FileOutputStream(ret)) {
        ServerUtil.copyStreamsAndCloseIn(in, out);
      }
    }

    return ret;
  }

  @SuppressWarnings("SameParameterValue")
  private Map<Object, Map<String, Object>> loadTable(String keyField, String table) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("select * from " + table)) {
      try (ResultSet rs = ps.executeQuery()) {

        List<String> cols = null;

        Map<Object, Map<String, Object>> ret = new HashMap<>();

        while (rs.next()) {

          if (cols == null) {
            cols = new ArrayList<>();
            for (int i = 1, n = rs.getMetaData().getColumnCount(); i <= n; i++) {
              cols.add(rs.getMetaData().getColumnName(i));
            }
          }

          HashMap<String, Object> record = new HashMap<>();
          for (String col : cols) {
            record.put(col, rs.getObject(col));
          }

          ret.put(record.get(keyField), record);

        }

        return ret;

      }
    }
  }
}