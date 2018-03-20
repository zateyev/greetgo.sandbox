package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MigrationTestDao {
  void cleanDb();

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date as dateOfBirth, charm_name as charmName FROM ${tableName}")
  List<Client> loadClientsList(@Param("tableName") String tableName);

  @Select("SELECT type, street, house, flat FROM ${tableName}")
  List<Address> loadAddressesList(@Param("tableName") String tableName);

  @Select("SELECT type, phone_number as number FROM ${tableName}")
  List<PhoneNumber> loadPhoneNumbersList(@Param("tableName") String tableName);

  @Select("DROP TABLE ${tmpClientTableName}, ${tmpAddrTableName}, ${tmpPhoneTableName}")
  void dropTmpTables(@Param("tmpClientTableName") String tmpClientTableName,
                     @Param("tmpAddrTableName") String tmpAddrTableName,
                     @Param("tmpPhoneTableName") String tmpPhoneTableName);

  @Select("CREATE TABLE tmp_client (\n" +
    "        cia_id VARCHAR(32),\n" +
    "        client_id VARCHAR(32),\n" +
    "        name VARCHAR(255),\n" +
    "        surname VARCHAR(255),\n" +
    "        patronymic VARCHAR(255),\n" +
    "        gender VARCHAR(12),\n" +
    "        birth_date DATE,\n" +
    "        charm_name VARCHAR(32),\n" +
    "        status INT NOT NULL DEFAULT 0,\n" +
    "        error VARCHAR(255),\n" +
    "        number INTEGER PRIMARY KEY\n" +
    "      );\n" +
    "CREATE TABLE tmp_addr (\n" +
    "        cia_id VARCHAR(32),\n" +
    "        client_num INTEGER,\n" +
    "        client_id VARCHAR(32),\n" +
    "        type VARCHAR(32),\n" +
    "        street VARCHAR(255),\n" +
    "        house VARCHAR(32),\n" +
    "        flat VARCHAR(32)\n" +
    "      );\n" +
    "CREATE TABLE tmp_phone (\n" +
    "        client_num INTEGER,\n" +
    "        client_id VARCHAR(32),\n" +
    "        phone_number VARCHAR(32),\n" +
    "        type VARCHAR(32),\n" +
    "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
    "        status INT NOT NULL DEFAULT 0,\n" +
    "        error VARCHAR(255),\n" +
    "        number BIGSERIAL PRIMARY KEY\n" +
    "      )")
  void createTmpTables();

  @Select("DROP TABLE IF EXISTS TMP_CLIENT, TMP_ADDR, TMP_PHONE")
  void dropCiaTmpTables();
}
