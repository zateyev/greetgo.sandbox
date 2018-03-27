package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.Client;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MigrationTestDao {
  void cleanDb();

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName} ORDER BY phone_number")
  List<Client> loadClientsList(@Param("tableName") String tableName);

  @Select("SELECT type, street, house, flat FROM ${tableName}")
  List<Address> loadAddressesList(@Param("tableName") String tableName);

  @Select("SELECT type, phone_number as phone_number FROM ${tableName}")
  List<PhoneNumber> loadPhoneNumbersList(@Param("tableName") String tableName);

  @Select("DROP TABLE ${tmpClientTableName}, ${tmpAddrTableName}, ${tmpPhoneTableName}")
  void dropTmpTables(@Param("tmpClientTableName") String tmpClientTableName,
                     @Param("tmpAddrTableName") String tmpAddrTableName,
                     @Param("tmpPhoneTableName") String tmpPhoneTableName);

  void insertClient(String tableName, Client client);

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName}" +
    " WHERE error IS NOT NULL ORDER BY phone_number")
  List<Client> loadErrorClientsList(@Param("tableName") String tableName);

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName}" +
    " WHERE status = 0 ORDER BY phone_number")
  List<Client> loadUniqueClientsList(@Param("tableName") String tableName);

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName}" +
    " WHERE status = 3 ORDER BY phone_number")
  List<Client> loadExistingClientsList(@Param("tableName") String tableName);

  void insertAddress(String tableName, Address address);

  void insertPhoneNumber(String tmpPhoneTable, PhoneNumber phoneNumber);

  @Select("SELECT type, phone_number FROM ${tableName} WHERE status = 0 ORDER BY number")
  List<PhoneNumber> loadUniquePhoneNumbers(@Param("tableName") String tableName);
}
