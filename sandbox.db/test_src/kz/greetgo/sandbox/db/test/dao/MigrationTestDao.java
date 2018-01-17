package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;
import org.apache.ibatis.annotations.*;
import org.testng.annotations.Test;

import java.util.List;

public interface MigrationTestDao {


  @Insert("insert into ${tableName}(no, cia_id, name, surname, patronymic, gender, birth, charm, generatedId)" +
    " values (#{no}, #{cia_id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birth}, #{charm}, #{genId})")
  void insertClient(
    @Param("tableName") String tableName,
    @Param("no") int no,
    @Param("cia_id") String ciaId,
    @Param("name") String name,
    @Param("surname") String surname,
    @Param("patronymic") String patronymic,
    @Param("gender") String gender,
    @Param("birth") String birth,
    @Param("charm") String charm,
    @Param("genId") String idGen);

  @Insert("insert into ${tableName}(no, client, type, street, house, flat)" +
    " values(#{index}, #{clientId}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(
    @Param("tableName") String tableName,
    @Param("index") int index,
    @Param("clientId") String clientId,
    @Param("type") String type,
    @Param("street") String street,
    @Param("house") String house,
    @Param("flat") String flat);

  @Insert("insert into ${tableName}(no, client, type, number)" +
    " values(#{index}, #{ciaId}, #{type}, #{number})")
  void insertPhone(
    @Param("tableName") String tableName,
    @Param("index") int index,
    @Param("ciaId") String ciaId,
    @Param("type") String type,
    @Param("number") String number);


  @Delete("drop table ${tableName};")
  void dropTables(
    @Param("tableName") String clientTable
  );

  @Select("select cia_id as client_id, account_number, registered_at " +
    " from ${tableName} where cia_id = #{ciaId}")
  List<Account> getAccounts(@Param("tableName") String tableName,
                            @Param("ciaId") String ciaId);

  @Select("select money, finished_at, transaction_type, account_number " +
    " from ${tableName} where account_number = #{accNum}")
  List<Transaction> getTransactions(@Param("tableName") String tableName,
                                    @Param("accNum") String accNum);

  @Insert("insert into ${tableName}( cia_id, account_number, registered_at, generatedId, no )" +
                        " values (#{ciaId}, #{accountNumber}, #{registeredAt}, #{generatedId}, #{no})")
  void insertAccount(
    @Param("tableName") String tableName,
    @Param("ciaId") String ciaId,
    @Param("accountNumber") String accountNumber,
    @Param("registeredAt") String registeredAt,
    @Param("generatedId") String generatedId,
    @Param("no") long no
  );


  @Insert("insert into ${tableName}(money, finished_at, transaction_type, account_number, generatedId)" +
    " values (#{money}, #{finishedAt}, #{transactionType}, #{accountNumber}, #{generatedId})")
  void insertTransaction(
    @Param("tableName") String tableName,
    @Param("money") String money,
    @Param("finishedAt") String finishedAt,
    @Param("transactionType") String transactionType,
    @Param("accountNumber") String accountNumber,
    @Param("generatedId") String generatedId
  );

  @Update("update client_account_transaction set actual = 0")
  void deleteAllTransactions();

  @Update("update transaction_type set actual = 0")
  void deleteAllTransactionType();

}
