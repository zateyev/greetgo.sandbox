package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.register.model.UserParamName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AuthTestDao {
  @Select("select value from UserParams where personId = #{personId} and name = #{name}")
  String loadParamValue(@Param("personId") String personId, @Param("name") UserParamName paramName);

  @Select("select count(1) from UserParams where personId = #{personId} and name = #{name} and value is not null")
  int countOfUserParams(@Param("personId") String personId, @Param("name") UserParamName paramName);

  @Insert("insert into UserParams (personId, name, value) values (#{personId}, #{name}, #{value})")
  void insertUserParam(@Param("personId") String personId,
                       @Param("name") UserParamName name,
                       @Param("value") String value);

  @Insert("insert into Person (id, accountName, encryptedPassword, blocked) " +
    "values (#{id}, #{accountName}, #{encryptedPassword}, #{blocked})")
  void insertUser(@Param("id") String id,
                  @Param("accountName") String accountName,
                  @Param("encryptedPassword") String encryptedPassword,
                  @Param("blocked") int blocked
  );

}
