package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientPhones;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface ClientTestDao {

  @Select("Select actual from client where id = #{id}")
  int getActualClient(@Param("id") String id);

  @Select("Select c.surname || ' ' || c.name || ' ' || c.patronymic AS fio, " +
    " ch.name AS charm, " +
    " extract(year from age(c.birth_date)) as age" +
    " from client c join charm ch on (c.charm_id = ch.id) where c.actual = 1 and ch.actual = 1")
  ClientRecord getClient(@Param("id") String id);

  @Select("Select c.current_gender gender," +
    " c.charm_id charmId," +
    " c.birth_date dateOfBirth," +
    " c.* " +
    " from Client c where id = #{id}")
  ClientDetails loadDetails(@Param("id") String id);

  @Insert("insert into client (id, name, surname, patronymic, birth_date, current_gender, charm_id, actual) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{birth_date}, #{gender}, #{charm_id}, 1);")
  void insertClient(@Param("id") String id,
                    @Param("name") String name,
                    @Param("surname") String surname,
                    @Param("patronymic") String patronymic,
                    @Param("birth_date") java.sql.Date birthDate,
                    @Param("gender") String gender,
                    @Param("charm_id") String charmId);


  @Update("update charm set actual = 0")
  void deleteAllCharms();

  @Insert("insert into charm (id, name, actual) values (#{id}, #{name}, 1)")
  void insertCharm(@Param("id") String id, @Param("name") String name);

  @Insert("insert into client (id) values (#{clientId})")
  void insert(@Param("clientId") String clientId);

  @Update("update client set ${fieldName} = #{value} where id = #{clientId}")
  void update(@Param("clientId") String clientId,
              @Param("fieldName") String fieldName,
              @Param("value") Object value);

  @Update("update client set actual = 0")
  void deleteAllClients();

  @Select("select c.id from client c"+
    " join charm ch on c.charm_id = ch.id"+
    " join client_account c_ac on c_ac.client = c.id"+
    " where 1=1 and c.actual = 1 group by c.id, ch.name")
  List<String> getListOfIds();

  @Insert("insert into client_addr(client, type, street, house, flat, actual)" +
    "values (#{id}, #{type}, #{street}, #{house}, #{flat}, 1)")
  void insertAdrr(@Param("id") String clientId,
                  @Param("type") String type,
                  @Param("street") String street,
                  @Param("house") String house,
                  @Param("flat") String flat);

  @Select("Select street, house, flat from client_addr where client = #{id} and type = 'fact' and actual = 1")
  ClientAddress getFactAddress(@Param("id") String id);

  @Select("Select street, house, flat from client_addr where client = #{id} and type = 'reg' and actual = 1")
  ClientAddress getRegAddress(@Param("id") String id);

  @Select("Select number from client_phone where client = #{id} and type = 'home' and actual = 1")
  String getHomePhone(@Param("id") String id);

  @Select("Select number from client_phone where client = #{id} and type = 'work' and actual = 1")
  String getWorkPhone(@Param("id")  String id);

  @Select("Select number from client_phone where client = #{id} and type = 'mobile' and actual = 1")
  List<String> getMobile(@Param("id")  String id);

  @Update("update client_phone set actual = 0")
  void deleteAllPhones();

  @Update("update client_addr set actual = 0")
  void deleteAllAddr();

  @Insert("insert into client_phone(client, type, number, actual) values (#{id}, #{type}, #{number}, 1)")
  void insertPhones(@Param("id") String clientId,
                    @Param("type") String type,
                    @Param("number") String number);

  @Insert("insert into client_account(id, client, money, number, registered_at, actual)" +
    "values (#{id}, #{client}, #{money}, #{number}, #{registered_at}, 1)")
  void insertClientAccount(
    @Param("id") String id,
    @Param("client") String client,
    @Param("money") float money,
    @Param("number") String number,
    @Param("registered_at") Date registeredAt);

  @Update("update client_account set actual = 0")
  void deleteAllAccounts();

  @Select("select number from client_account where client = #{id} and actual = 1")
  String getClientAccount(@Param("id") String clientId);
}
