package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Bean
public interface ClientDaoPostgres extends ClientDao {
  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}) " +
    "ON CONFLICT (id) DO UPDATE SET surname = #{surname}, name = #{name}, patronymic = #{patronymic}, " +
    "gender = #{gender}, birth_date = #{birth_date}, charm = #{charm}")
  void insertOrUpdateClient(@Param("id") String clientId,
                            @Param("surname") String surname,
                            @Param("name") String name,
                            @Param("patronymic") String patronymic,
                            @Param("gender") Gender gender,
                            @Param("birth_date") Date birth_date,
                            @Param("charm") String charmId);

  @Insert("INSERT INTO client_phone (client, number, type) VALUES (#{client}, #{number}, #{type}) " +
    "ON CONFLICT (client, number) DO UPDATE SET type = #{type}")
  void insertPhoneNumber(@Param("client") String clientId,
                         @Param("number") String number,
                         @Param("type") PhoneType type);

  @Insert("INSERT INTO client_addr (client, type, street, house, flat) VALUES (#{client}, #{type}, #{street}, #{house}, #{flat}) " +
    "ON CONFLICT (client, type) DO UPDATE SET  street = #{street}, house = #{house}, flat = #{flat}")
  void insertAddress(@Param("client") String clientId,
                     @Param("type") AddressType type,
                     @Param("street") String street,
                     @Param("house") String house,
                     @Param("flat") String flat);
}
