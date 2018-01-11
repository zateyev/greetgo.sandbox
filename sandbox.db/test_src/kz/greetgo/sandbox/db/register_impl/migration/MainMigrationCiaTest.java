package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class MainMigrationCiaTest extends ParentTestNg {

  public BeanGetter<MigrationManager> migrationManager;
  public BeanGetter<MigrationTestDao> migrationTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;

  Connection connection;
  MigrationCia migration = new MigrationCia();

  @BeforeMethod
  public void createConnection() throws Exception {
    connection = migrationManager.get().createConnection();
    migration.connection = connection;
    migration.createTempTables();
  }

  @AfterMethod
  public void closeConnection() throws Exception {
    connection.close();
    connection = null;
  }

  @Test
  public void mainMigration_CheckForValidData() throws Exception {


    migrationTestDao.get().insertClient(
      migration.clientTable,
      1,
      "cia_id1",
      "Vasya",
      "Pupkin",
      "Vasilich",
      "male",
      "1999-12-12",
      "newCharm"
    );

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      1,
      "cia_id1",
      "reg",
      "regStreet",
      "regHouse",
      "regFlat"
    );
    migrationTestDao.get().insertAddress(
      migration.addressTable,
      1,
      "cia_id1",
      "fact",
      "factStreet",
      "factHouse",
      "factFlat"
    );

    insertTmpPhone(1, "cia_id1", "home", "123123");
    insertTmpPhone(1, "cia_id1", "mobile", "456456");
    insertTmpPhone(1, "cia_id1", "work", "789789");



    //
    //
    migration.mainMigrationOperation();
    //
    //

    ClientDetails det = clientTestDao.get().getClientByCiaId("cia_id1");
    String clientCharm = clientTestDao.get().getClientCharmByCiaId("cia_id1");
    ClientAddress reg = clientTestDao.get().getRegAddress(det.id);
    ClientAddress fact = clientTestDao.get().getFactAddress(det.id);
    List<String> homePhone = clientTestDao.get().getHomePhones(det.id);
    List<String> mobilePhone = clientTestDao.get().getMobilePhones(det.id);
    List<String> workPhone = clientTestDao.get().getWorkPhones(det.id);

    assertThat(det.name).isEqualTo("Vasya");
    assertThat(det.surname).isEqualTo("Pupkin");
    assertThat(det.patronymic).isEqualTo("Vasilich");
    assertThat(det.gender).isEqualTo("male");
    assertThat(det.dateOfBirth).isEqualTo("1999-12-12");
    assertThat(clientCharm).isEqualTo("newCharm");
    assertThat(reg.street).isEqualTo("regStreet");
    assertThat(reg.house).isEqualTo("regHouse");
    assertThat(reg.flat).isEqualTo("regFlat");
    assertThat(fact.street).isEqualTo("factStreet");
    assertThat(fact.house).isEqualTo("factHouse");
    assertThat(fact.flat).isEqualTo("factFlat");
    assertThat(homePhone.get(0)).isEqualTo("123123");
    assertThat(mobilePhone.get(0)).isEqualTo("456456");
    assertThat(workPhone.get(0)).isEqualTo("789789");


  }

  @Test
  public void mainMigration_CheckForSimilarCiaId() throws SQLException {
    deleteAll();

    migrationTestDao.get().insertClient(
      migration.clientTable,
      1,
      "cia_id1",
      "Vasya",
      "Pupkin",
      "Vasilich",
      "male",
      "1999-12-12",
      "newCharm"
    );

    insertTmpAdress(1, "cia_id1", "reg");
    insertTmpAdress(1, "cia_id1", "fact");
    insertTmpPhone(1, "cia_id1", "home", "555");
    insertTmpPhone(1, "cia_id1", "mobile", "666");
    insertTmpPhone(1, "cia_id1", "work", "999");

    migrationTestDao.get().insertClient(
      migration.clientTable,
      2,
      "cia_id1",
      "newVasya",
      "newPupkin",
      "newVasilich",
      "female",
      "1990-12-12",
      "Charm"
    );
    migrationTestDao.get().insertAddress(
      migration.addressTable,
      2,
      "cia_id1",
      "reg",
      "regStreet",
      "regHouse",
      "regFlat"
    );
    migrationTestDao.get().insertAddress(
      migration.addressTable,
      2,
      "cia_id1",
      "fact",
      "factStreet",
      "factHouse",
      "factFlat"
    );
    insertTmpPhone(2, "cia_id1", "home", "666");
    insertTmpPhone(2, "cia_id1", "mobile", "555");
    insertTmpPhone(2, "cia_id1", "work", "999");


    //
    //
    migration.mainMigrationOperation();
    //
    //

    ClientDetails det = clientTestDao.get().getClientByCiaId("cia_id1");
    String clientCharm = clientTestDao.get().getClientCharmByCiaId("cia_id1");
    int sizeOfCharms = clientTestDao.get().getCharmListSize();
    ClientAddress reg = clientTestDao.get().getRegAddress(det.id);
    ClientAddress fact = clientTestDao.get().getFactAddress(det.id);
    List<String> homePhone = clientTestDao.get().getHomePhones(det.id);
    List<String> mobilePhone = clientTestDao.get().getMobilePhones(det.id);
    List<String> workPhone = clientTestDao.get().getWorkPhones(det.id);


    assertThat(sizeOfCharms).isEqualTo(2);

    assertThat(det.name).isEqualTo("newVasya");
    assertThat(det.surname).isEqualTo("newPupkin");
    assertThat(det.patronymic).isEqualTo("newVasilich");
    assertThat(det.gender).isEqualTo("female");
    assertThat(det.dateOfBirth).isEqualTo("1990-12-12");
    assertThat(clientCharm).isEqualTo("Charm");
    assertThat(reg.street).isEqualTo("regStreet");
    assertThat(reg.house).isEqualTo("regHouse");
    assertThat(reg.flat).isEqualTo("regFlat");
    assertThat(fact.street).isEqualTo("factStreet");
    assertThat(fact.house).isEqualTo("factHouse");
    assertThat(fact.flat).isEqualTo("factFlat");
    assertThat(homePhone.get(0)).isEqualTo("666");
    assertThat(mobilePhone.get(0)).isEqualTo("555");
    assertThat(workPhone.get(0)).isEqualTo("999");


  }

  @Test
  public void mainMigration_CheckForCharm() throws SQLException {
    String charmId = RND.str(10);
    String charmName = RND.str(10);

    deleteAll();
    insertCharm(charmId, charmName);

    insertTmpClientWithCharm(
      1,
      charmName,
      "cia_id12"
    );

    //
    //
    migration.mainMigrationOperation();
    //
    //

    String clientCharm = clientTestDao.get().getClientCharmByCiaId("cia_id12");
    int sizeOfCharms = clientTestDao.get().getCharmListSize();

    assertThat(clientCharm).isEqualTo(charmName);
    assertThat(sizeOfCharms).isEqualTo(1);


  }

  @Test
  public void mainMigration_CheckForSize() throws SQLException {

    deleteAll();

    for (int i = 0; i < 50; i++) {
      insertTmpClient(i);
    }

    //
    //
    migration.mainMigrationOperation();
    //
    //

    int clientListSize = clientTestDao.get().getClientListSize();
    int charmListSize = clientTestDao.get().getCharmListSize();

    assertThat(clientListSize).isEqualTo(50);
    assertThat(charmListSize).isEqualTo(50);

  }

  @Test
  public void mainMigration_CheckForNullClientFields() throws SQLException {

    deleteAll();

    insertTmpClient(11);
    insertTmpClient(12);
    insertTmpClient(13);

    insertTmpClientNullName(22);
    insertTmpClientNullSurname(23);
    insertTmpClientNullBirth(24);
    insertTmpClientNullCharm(25);
    insertTmpClientNullGender(26);

    //
    //
    migration.mainMigrationOperation();
    //
    //

    int clientListSize = clientTestDao.get().getClientListSize();

    assertThat(clientListSize).isEqualTo(3);

  }

  @Test
  public void mainMigration_CheckForNullAddress() throws SQLException {

    deleteAll();

    insertTmpClientWithCiaID(1, "ciaId1");
    insertTmpClientWithCiaID(2, "ciaId2");
    insertTmpClientWithCiaID(3, "ciaId3");

    insertTmpAdress(1, "ciaId1", "reg");
    insertTmpAdress(1, "ciaId1", "fact");
    insertTmpAdress(2, "ciaId2", "reg");
    insertTmpAdress(2, "ciaId2", "fact");

    insertTmpAdressNullStreet(3, "ciaId3", "reg");
    insertTmpAdressNullHouse(3, "ciaId3", "fact");


    //
    //
    migration.mainMigrationOperation();
    //
    //


    int clientListSize = clientTestDao.get().getClientListSize();

    assertThat(clientListSize).isEqualTo(2);


  }

  @Test
  public void mainMigration_CheckForPhone() throws SQLException {

    deleteAll();

    insertTmpClientWithCiaID(1, "ciaId1");
    insertTmpAdress(1, "ciaId1", "reg");
    insertTmpAdress(1, "ciaId1", "fact");
    insertTmpPhone(1, "ciaId1", "home", "789789");
    insertTmpPhone(1, "ciaId1", "work", "987987");

    insertTmpClientWithCiaID(2, "ciaId2");
    insertTmpAdress(2, "ciaId2", "reg");
    insertTmpAdress(2, "ciaId2", "fact");
    insertTmpPhone(2, "ciaId2", "work", "123");

    insertTmpClientWithCiaID(3, "ciaId3");
    insertTmpAdress(3, "ciaId3", "reg");
    insertTmpAdress(3, "ciaId3", "fact");

    //
    //
    migration.mainMigrationOperation();
    //
    //

    int clientListSize = clientTestDao.get().getClientListSize();

    assertThat(clientListSize).isEqualTo(2);

  }


  private void insertCharm(String charmId, String charmName) {
    clientTestDao.get().insertCharm(charmId, charmName);
  }


  private void insertTmpClient(int index) {

    String ciaId = RND.str(10);

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      ciaId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      "male",
      "1999-12-12",
      RND.str(6)
    );

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      "reg",
      RND.str(7),
      RND.str(6),
      RND.str(6)
    );
    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      "fact",
      RND.str(8),
      RND.str(6),
      RND.str(6)
    );

  }

  private void insertTmpClientNullName(int index) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      RND.str(10),
      null,
      RND.str(10),
      RND.str(10),
      "male",
      "1999-12-12",
      RND.str(6)
    );

  }

  private void insertTmpClientNullSurname(int index) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      RND.str(10),
      RND.str(10),
      null,
      RND.str(10),
      "male",
      "1999-12-12",
      RND.str(6)
    );

  }

  private void insertTmpClientNullBirth(int index) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      RND.str(10),
      "male",
      null,
      RND.str(6)
    );

  }

  private void insertTmpClientNullGender(int index) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      RND.str(10),
      null,
      "1999-12-12",
      RND.str(6)
    );

  }

  private void insertTmpClientNullCharm(int index) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      RND.str(10),
      "male",
      "1999-12-12",
      null
    );

  }

  private void insertTmpClientWithCiaID(int index, String ciaId) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      ciaId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      "male",
      "1999-12-12",
      RND.str(6)
    );

  }

  private void insertTmpClientWithCharm(int index, String charmName, String ciaId) {

    migrationTestDao.get().insertClient(
      migration.clientTable,
      index,
      ciaId,
      RND.str(10),
      RND.str(10),
      RND.str(10),
      "male",
      "1999-12-12",
      charmName
    );

  }

  private void insertTmpAdress(int index, String ciaId, String type) {

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      type,
      RND.str(6),
      RND.str(6),
      RND.str(6)
    );

  }

  private void insertTmpAdressNullStreet(int index, String ciaId, String type) {

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      type,
      null,
      RND.str(6),
      RND.str(6)
    );

  }

  private void insertTmpAdressNullHouse(int index, String ciaId, String type) {

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      type,
      RND.str(6),
      null,
      RND.str(6)
    );

  }

  private void insertTmpAdressNullFlat(int index, String ciaId, String type) {

    migrationTestDao.get().insertAddress(
      migration.addressTable,
      index,
      ciaId,
      type,
      RND.str(6),
      RND.str(6),
      null
    );

  }

  private void insertTmpPhone(int index, String ciaId, String type, String phone){

    migrationTestDao.get().insertPhone(
      migration.phoneTable,
      index,
      ciaId,
      type,
      phone
    );

  }


  private void deleteAll() {
    clientTestDao.get().deleteAllAddr();
    clientTestDao.get().deleteAllPhones();
    clientTestDao.get().deleteAllCharms();
    clientTestDao.get().deleteAllClients();
    clientTestDao.get().deleteAllAccounts();
  }

}
