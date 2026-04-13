//package com.example.quizcard.controller;
//
//import com.example.quizcard.flinkapp.model.UserSummary;
//import com.example.quizcard.flinkapp.util.UserProfileDAO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class DevApi {
//
//    @Autowired
//    UserProfileDAO userProfileDAO;
//
//    @PostMapping("/create")
//    public String createUser(@RequestParam String name, @RequestParam String email) {
//        userProfileDAO.createUserProfile(email, name, email);
//        return email;
//    }
//
//    @GetMapping("/query")
//    public UserSummary getUser(@RequestParam String accountId) {
//        return userProfileDAO.getUserProfile(accountId);
//    }
//}
