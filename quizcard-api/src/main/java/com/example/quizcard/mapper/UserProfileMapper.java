package com.example.quizcard.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserProfileMapper {

    @Insert("INSERT INTO user_profile (account_id, name, email) VALUES (#{accountId}, #{name}, #{email})")
    void insertUserProfile(@Param("accountId") String accountId, @Param("name") String name, @Param("email") String email);

    @Select("SELECT * FROM user_profile WHERE account_id = #{accountId}")
    List<Map<String, Object>> selectProfileById(@Param("accountId") String accountId);
}