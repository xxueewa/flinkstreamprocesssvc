//package com.example.quizcard.flinkapp.util;
//
//import com.example.quizcard.flinkapp.mapper.UserProfileMapper;
//import com.example.quizcard.flinkapp.model.UserSummary;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class UserErrorRateDAO {
//
//    private final UserProfileMapper userProfileMapper;
//
//    public UserErrorRateDAO(UserProfileMapper userProfileMapper) {
//        this.userProfileMapper = userProfileMapper;
//    }
//
//    public UserSummary queryErrorRates(String accountId) {
//        List<Map<String, Object>> records = userProfileMapper.findErrorRatesByAccountId(accountId);
//
//        Map<String, Double> errorRates = new HashMap<>();
//        records.forEach(row -> {
//            String subject = row.get("subject").toString();
//            Double errorRate = ((Number) row.get("error_rate")).doubleValue();
//            errorRates.put(subject, errorRate);
//        });
//
//        UserSummary userSummary = new UserSummary();
//        userSummary.setAccountId(accountId);
//        userSummary.setErrorRates(errorRates);
//        return userSummary;
//    }
//}