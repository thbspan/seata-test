package org.test.seata.httpclient.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AccountDao {

    @Select("SELECT balance FROM account WHERE id = #{userId}")
    Integer getBalance(@Param("userId")Long userId);

    @Update("UPDATE account SET balance = balance - #{price} WHERE id = ${userId} AND balance >= ${price}")
    int reduceBalance(@Param("userId")Long userId, @Param("price")Integer price);
}
