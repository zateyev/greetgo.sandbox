package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface MigrationTestDao {


  @Insert("insert into ${tableName}(no, cia_id, name, surname, patronymic, gender, birth, charm)" +
    " values (#{no}, #{cia_id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birth}, #{charm})")
  void insertClient(
    @Param("tableName") String tableName,
    @Param("no") int no,
    @Param("cia_id") String ciaId,
    @Param("name") String name,
    @Param("surname") String surname,
    @Param("patronymic") String patronymic,
    @Param("gender") String gender,
    @Param("birth") String birth,
    @Param("charm") String charm);

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
}
