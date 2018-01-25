package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientPhoneDao {
  @Select("SELECT number, type FROM client_phone WHERE client=#{client} AND record_state=0")
  List<Phone> selectRowsByClient(@Param("client") long client);

  void insert(long client, String number, String type);

  @Update("UPDATE client_phone " +
    "SET number=#{number}, type=#{type} " +
    "WHERE client=#{client}")
  void update(@Param("client") long client,
              @Param("number") String number,
              @Param("type") String type);

  @Update("UPDATE client_phone " +
    "SET record_state=1 " +
    "WHERE client=#{client} AND number=#{number}")
  void updateSetDisabled(@Param("client") long client,
                         @Param("number") String number);
}
