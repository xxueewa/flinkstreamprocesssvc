package com.example.quizcard.controller;

import com.example.quizcard.flinkapp.util.UserProfileDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DevApi {

    @Autowired
    UserProfileDAO userProfileDAO;

    @PostMapping("/create")
    public String createUser(@RequestParam String name, @RequestParam String email) {
        String accountId = UUID.randomUUID().toString();
        userProfileDAO.createUserProfile(accountId, name, email);
        return accountId;
    }
}
