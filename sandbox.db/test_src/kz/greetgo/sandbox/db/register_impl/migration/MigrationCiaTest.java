package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.migration.models.Address;
import kz.greetgo.sandbox.db.register_impl.migration.models.Phone;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
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

  public BeanGetter<ClientTestDao> clientTestDao;

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
    Map<String, Address> address = getAddressById(migration.addressTable, "4-DU8-32-H7");
    Phone phones = getPhoneById(migration.phoneTable, "4-DU8-32-H7");

    assertThat(client).hasSize(2);

    assertThat(client.get(1L).get("cia_id")).isEqualTo("4-DU8-32-H7");
    assertThat(client.get(1L).get("surname")).isEqualTo("Иванов");
    assertThat(client.get(1L).get("name")).isEqualTo("Иван");
    assertThat(client.get(1L).get("patronymic")).isEqualTo("Иваныч");
    assertThat(client.get(1L).get("gender")).isEqualTo("MALE");
    assertThat(client.get(1L).get("charm")).isEqualTo("Уситчивый");
    assertThat(client.get(1L).get("birth")).isEqualTo("1980-11-12");

    assertThat(address.get("fact").clientId).isEqualTo("4-DU8-32-H7");
    assertThat(address.get("fact").street).isEqualTo("Панфилова");
    assertThat(address.get("fact").house).isEqualTo("23A");
    assertThat(address.get("fact").flat).isEqualTo("22");

    assertThat(address.get("reg").clientId).isEqualTo("4-DU8-32-H7");
    assertThat(address.get("reg").street).isEqualTo("Абая");
    assertThat(address.get("reg").house).isEqualTo("24A");
    assertThat(address.get("reg").flat).isEqualTo("2");

    assertThat(phones.home).hasSize(1);
    assertThat(phones.mobile).hasSize(3);
    assertThat(phones.work).hasSize(2);

    assertThat(phones.home.get(0)).isEqualTo("+7-123-111-22-33");
    assertThat(phones.mobile.get(0)).isEqualTo("+7-123-111-33-33");
    assertThat(phones.mobile.get(1)).isEqualTo("+7-123-111-44-33");
    assertThat(phones.mobile.get(2)).isEqualTo("+7-123-111-55-33");
    assertThat(phones.work.get(0)).isEqualTo("+7-123-111-00-33 вн. 3343");
    assertThat(phones.work.get(1)).isEqualTo("+7-123-111-00-33 вн. 3344");

  }

  @Test
  public void uploadFileToTmpTable_invalidFile() throws Exception {

    MigrationCia migration = new MigrationCia();
    migration.connection = connection;
    migration.inFile = createInFile("cia_test_invalid.xml");

    migration.createTempTables();
    migration.uploadFileToTempTables();

    Map<Object, Map<String, Object>> client = loadTable("no", migration.clientTable);
    Map<String, Address> address = getAddressById(migration.addressTable, "4-DU8-32-H7");
    Phone phones = getPhoneById(migration.phoneTable, "4-DU8-32-H7");

    assertThat(client).hasSize(1);

    assertThat(client.get(1L).get("cia_id")).isEqualTo("4-DU8-32-H7");
    assertThat(client.get(1L).get("surname")).isNull();
    assertThat(client.get(1L).get("name")).isNull();
    assertThat(client.get(1L).get("patronymic")).isNull();
    assertThat(client.get(1L).get("gender")).isEqualTo("MALE");
    assertThat(client.get(1L).get("charm")).isEqualTo("Уситчивый");
    assertThat(client.get(1L).get("birth")).isEqualTo("1980-11-12");

    assertThat(address.get("fact").clientId).isEqualTo("4-DU8-32-H7");
    assertThat(address.get("fact").street).isNull();
    assertThat(address.get("fact").house).isNull();
    assertThat(address.get("fact").flat).isNull();

    assertThat(address.get("reg").clientId).isEqualTo("4-DU8-32-H7");
    assertThat(address.get("reg").street).isEqualTo("Абая");
    assertThat(address.get("reg").house).isEqualTo("24A");
    assertThat(address.get("reg").flat).isEqualTo("2");

    assertThat(phones.home).hasSize(1);
    assertThat(phones.mobile).hasSize(3);
    assertThat(phones.work).hasSize(0);

    assertThat(phones.home.get(0)).isEqualTo("+7-123-111-22-33");
    assertThat(phones.mobile.get(0)).isEqualTo("+7-123-111-33-33");
    assertThat(phones.mobile.get(1)).isEqualTo("+7-123-111-44-33");
    assertThat(phones.mobile.get(2)).isEqualTo("+7-123-111-55-33");

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

  private Map<String, Address> getAddressById(String table, String client) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("select * from " + table + " where client  = ?")) {

      {
        ps.setString(1, client);
      }

      try (ResultSet rs = ps.executeQuery()) {

        Map<String, Address> map = new HashMap<>();

        while (rs.next()) {
          if ("reg".equals(rs.getString("type"))) {

            Address tmp = new Address();

            tmp.clientId = rs.getString("client");
            tmp.street = rs.getString("street");
            tmp.house = rs.getString("house");
            tmp.flat = rs.getString("flat");

            map.put("reg", tmp);
          }

          if ("fact".equals(rs.getString("type"))) {

            Address tmp = new Address();

            tmp.clientId = rs.getString("client");
            tmp.street = rs.getString("street");
            tmp.house = rs.getString("house");
            tmp.flat = rs.getString("flat");

            map.put("fact", tmp);
          }

        }

        return map;

      }
    }
  }

  private Phone getPhoneById(String table, String client) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("select * from " + table + " where client  = ? ORDER by NUMBER ")) {

      {
        ps.setString(1, client);
      }

      try (ResultSet rs = ps.executeQuery()) {

        Phone p = new Phone();

        while (rs.next()) {
          if ("work".equals(rs.getString("type"))) {
            p.work.add(rs.getString("number"));
          }

          if ("home".equals(rs.getString("type"))) {
            p.home.add(rs.getString("number"));
          }

          if ("mobile".equals(rs.getString("type"))) {
            p.mobile.add(rs.getString("number"));
          }
        }

        return p;
      }

    }
  }


}
