package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.*;

import java.sql.Date;
import java.sql.Timestamp;

public interface ClientTestDao {
  //Client table part
  @Delete("DELETE FROM client")
  void deleteAllTableClient();

  @Select("SELECT COUNT(*) FROM client WHERE disabled=false")
  long selectCountTableClient();

  @Select("SELECT EXISTS (SELECT true FROM client WHERE id=#{id})")
  boolean selectExistSingleTableClient(@Param("id") long id);

  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm);

  @Update("UPDATE client " +
    "SET surname=#{surname}, name=#{name}, patronymic=#{patronymic}, " +
    "gender=#{gender}, birth_date=#{birth_date}, charm=#{charm}" +
    "WHERE id=#{id}")
  void updateClient(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm);

  //Charm table part
  @Delete("DELETE FROM charm")
  void deleteAllTableCharm();

  @Select("SELECT nextval('charm_id_seq')")
  int selectSeqIdNextValueTableCharm();

  @Update("UPDATE charm SET disabled=true WHERE id=#{id}")
  void updateDisableSingleTableCharm(@Param("id") int id);

  @Insert("INSERT INTO charm (id, name, description, energy) " +
    "VALUES (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") int id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") float energy);

  //Client_Account table part
  @Delete("DELETE FROM client_account")
  void deleteAllTableClientAccount();

  @Select("SELECT nextval('client_account_id_seq')")
  long selectSeqIdNextValueTableClientAccount();

  @Insert("INSERT INTO client_account (id, client, money, number, registered_at) " +
    "VALUES (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(@Param("id") long id,
                           @Param("client") long client,
                           @Param("money") float money,
                           @Param("number") String number,
                           @Param("registered_at") Timestamp registered_at);

  //Client_Addr table part
  @Delete("DELETE FROM client_addr")
  void deleteAllTableClientAddr();

  @Insert("INSERT INTO client_addr (client, type, street, house, flat) " +
    "VALUES (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddr(@Param("client") long client,
                        @Param("type") String type,
                        @Param("street") String street,
                        @Param("house") String house,
                        @Param("flat") String flat);

  //Client_Phone table part
  @Delete("DELETE FROM client_phone")
  void deleteAllTableClientPhone();

  @Insert("INSERT INTO client_phone (client, number, type) " +
    "VALUES (#{client}, #{number}, #{type})")
  void insertClientPhone(@Param("client") long client,
                         @Param("number") String number,
                         @Param("type") String type);
}
