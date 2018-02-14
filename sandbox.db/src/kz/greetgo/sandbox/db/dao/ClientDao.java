package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientDao {
  @Select("select count(*) from Client where position(#{filterInputs} in ${filterBy}) <> 0")
  long getTotalSize(@Param("filterBy") String filterBy,
                    @Param("filterInputs") String filterInputs);

//  @Select("select id from Person where accountName = #{accountName} " +
//    "and encryptedPassword = #{encryptedPassword} and blocked = 0")
//  String selectPersonIdByAccountAndPassword(@Param("accountName") String accountName,
//                                            @Param("encryptedPassword") String encryptedPassword);
//
//  @Select("select accountName from Person where id = #{personId}")
//  String accountNameByPersonId(@Param("personId") String personId);
//
//  @Select("select * from Person where id = #{personId}")
//  UserInfo getUserInfo(@Param("personId") String personId);
}
