package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration_impl.model.AccountTmp;
import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.TransactionTmp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MigrationTestDao {
  void cleanDb();

  @Select("SELECT cia_id, surname, name, patronymic, gender, birth_date, charm_name FROM ${tableName} ORDER BY number")
  List<ClientTmp> loadClientsList(@Param("tableName") String tableName);

  @Select("SELECT * FROM ${tableName} ORDER BY number")
  List<ClientTmp> selectAll(@Param("tableName") String tableName);

  @Select("SELECT type, street, house, flat FROM ${tableName}")
  List<Address> loadAddressesList(@Param("tableName") String tableName);

  @Select("SELECT type, phone_number FROM ${tableName} ORDER BY number")
  List<PhoneNumber> loadPhoneNumbersList(@Param("tableName") String tableName);

  void insertClient(String tableName, ClientTmp client);

  void insertAddress(String tableName, Address address);

  void insertPhoneNumber(String tmpPhoneTable, PhoneNumber phoneNumber);

  @Select("SELECT type, phone_number, status, number FROM ${tableName} ORDER BY number")
  List<PhoneNumber> loadUniquePhoneNumbers(@Param("tableName") String tableName);

  @Select("SELECT account_number, registered_at as registeredAtD FROM ${tableName} WHERE status = 0 ORDER BY number")
  List<AccountTmp> loadAccountsList(@Param("tableName") String tableName);

  @Select("SELECT money, transaction_type FROM ${tableName} WHERE status = 0 ORDER BY finished_at")
  List<TransactionTmp> loadTransactionsList(@Param("tableName") String tableName);

  void insertClientAccount(String tmpAccountTable, AccountTmp account);

  void insertAccountTransaction(String tmpTransactionTable, TransactionTmp transaction);
}
