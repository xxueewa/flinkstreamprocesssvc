package com.example.quizcard.flinkapp.util;

import com.example.quizcard.flinkapp.mapper.UserProfileMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserProfileDAO {

    private static final List<String> SUBJECTS = List.of("business", "law", "psychology", "biology", "chemistry",
            "history", "health", "economics", "math", "physics",
            "computer_science", "philosophy", "engineering", "other");

    private final UserProfileMapper userProfileMapper;

    public UserProfileDAO(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    @Transactional
    public void createUserProfile(String accountId, String name, String email) {
        userProfileMapper.insertUserProfile(accountId, name, email);
        userProfileMapper.insertErrorRatesForProfile(accountId, SUBJECTS);
    }
}