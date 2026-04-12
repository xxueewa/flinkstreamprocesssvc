package com.example.quizcard.flinkapp.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserProfileMapper {

    @Insert("<script>" +
            "INSERT INTO user_error_rate (account_id, subject, error_rate, last_update, created_time) VALUES " +
            "<foreach collection='subjects' item='subject' separator=','>" +
            "(#{accountId}, #{subject}, 0.0, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    void insertErrorRatesForProfile(@Param("accountId") String accountId, @Param("subjects") Set<String> subjects);

    @Insert("INSERT INTO user_profile (account_id, name, email) VALUES (#{accountId}, #{name}, #{email})")
    void insertUserProfile(@Param("accountId") String accountId, @Param("name") String name, @Param("email") String email);

    @Select("SELECT * FROM user_profile WHERE account_id = #{accountId}")
    List<Map<String, Object>> selectProfileById(@Param("accountId") String accountId);

    @Select("SELECT subject, error_rate, last_update FROM user_error_rate WHERE account_id = #{accountId}")
    List<Map<String, Object>> findErrorRatesByAccountId(@Param("accountId") String accountId);

    @Update("UPDATE user_error_rate SET error_rate=#{error_rate}, last_update=NOW() WHERE account_id = #{accountId} AND subject = #{subject}")
    void updateErrorRateByAccountIdAndSubject(@Param("accountId") String accountId,
                                              @Param("subject") String subject,
                                              @Param("error_rate") Double errorRate);

}