package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.AddressInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ClientAddrDao {
  @Select("SELECT street, house, flat, type " +
    "FROM client_addr " +
    "WHERE client=#{client} AND type=#{type}")
  AddressInfo selectRowByClientAndType(@Param("client") long client, @Param("type") String type);

  @Insert("INSERT INTO client_addr (client, type, street, house, flat) " +
    "VALUES(#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insert(@Param("client") long client,
              @Param("type") String type,
              @Param("street") String street,
              @Param("house") String house,
              @Param("flat") String flat);

  @Update("UPDATE client_addr " +
    "SET street=#{street}, house=#{house}, flat=#{flat} " +
    "WHERE client=#{client} AND type=#{type}")
  void update(@Param("client") long client,
              @Param("type") String type,
              @Param("street") String street,
              @Param("house") String house,
              @Param("flat") String flat);
}
