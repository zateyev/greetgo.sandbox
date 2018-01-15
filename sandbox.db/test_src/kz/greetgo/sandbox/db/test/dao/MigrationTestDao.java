package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.models.Account;
import kz.greetgo.sandbox.db.register_impl.migration.models.Transaction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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


  @Delete("drop table ${clientTable};" +
    " drop table ${phoneTable};" +
    " drop table ${addressTable}")
  void dropTables(
    @Param("clientTable") String clientTable,
    @Param("phoneTable") String phoneTable,
    @Param("addressTable") String addressTable);

  @Select("select cia_id as client_id, account_number, registered_at " +
    " from ${tableName} where cia_id = #{ciaId}")
  List<Account> getAccounts(@Param("tableName") String tableName,
                            @Param("ciaId") String ciaId);

  @Select("select money, finished_at, transaction_type, account_number " +
    " from ${tableName} where account_number = #{accNum}")
  List<Transaction> getTransactions(@Param("tableName") String tableName,
                                    @Param("accNum") String accNum);
}
