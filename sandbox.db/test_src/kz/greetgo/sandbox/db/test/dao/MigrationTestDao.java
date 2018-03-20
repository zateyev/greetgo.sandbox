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

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName} ORDER BY number")
  List<Client> loadClientsList(@Param("tableName") String tableName);

  @Select("SELECT type, street, house, flat FROM ${tableName}")
  List<Address> loadAddressesList(@Param("tableName") String tableName);

  @Select("SELECT type, phone_number as number FROM ${tableName}")
  List<PhoneNumber> loadPhoneNumbersList(@Param("tableName") String tableName);

  @Select("DROP TABLE ${tmpClientTableName}, ${tmpAddrTableName}, ${tmpPhoneTableName}")
  void dropTmpTables(@Param("tmpClientTableName") String tmpClientTableName,
                     @Param("tmpAddrTableName") String tmpAddrTableName,
                     @Param("tmpPhoneTableName") String tmpPhoneTableName);

  void insertClient(String tableName, Client client);

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName}" +
    " WHERE error IS NOT NULL ORDER BY number")
  List<Client> loadErrorClientsList(@Param("tableName") String tableName);
}
