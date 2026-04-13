//package com.example.quizcard.flinkapp.util;
//
//import com.example.quizcard.flinkapp.mapper.UserProfileMapper;
//import com.example.quizcard.flinkapp.model.UserSummary;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Component
//public class UserProfileDAO {
//
//    private static final Set<String> SUBJECTS = Set.of("business", "law", "psychology", "biology", "chemistry",
//            "history", "health", "economics", "math", "physics",
//            "computer_science", "philosophy", "engineering", "other");
//
//    private final UserProfileMapper userProfileMapper;
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
//
//    public UserProfileDAO(UserProfileMapper userProfileMapper) {
//        this.userProfileMapper = userProfileMapper;
//    }
//
//    @Transactional
//    public void createUserProfile(String accountId, String name, String email) {
//        userProfileMapper.insertUserProfile(accountId, name, email);
//        userProfileMapper.insertErrorRatesForProfile(accountId, SUBJECTS);
//    }
//
//    @Transactional
//    public UserSummary getUserProfile(String accountId) {
//        UserSummary userSummary = new UserSummary();
//        Map<String, Object> profileInfo = userProfileMapper.selectProfileById(accountId).get(0);
//        List<Map<String, Object>> errorRateRecords = userProfileMapper.findErrorRatesByAccountId(accountId);
//
//        userSummary.setName(profileInfo.get("name").toString());
//        userSummary.setAccountId(accountId);
//        userSummary.setEmail(profileInfo.get("email").toString());
//
//        Map<String, Double> errorRates = new HashMap<>();
//        AtomicLong lastUpdateTime = new AtomicLong(Long.MIN_VALUE);
//
//        errorRateRecords.forEach(e -> {
//            String subject = e.getOrDefault("subject", "").toString();
//            Double errorRate = (double) e.get("error_rate");
//            if (SUBJECTS.contains(subject)) {
//                errorRates.put(subject, errorRate);
//            }
//            String lastUpdate = e.get("last_update").toString();
//            long lastUpdateMilli = LocalDateTime.parse(lastUpdate, formatter)
//                    .toInstant(ZoneOffset.of("-07:00"))
//                    .toEpochMilli();
//            lastUpdateTime.set(Math.max(lastUpdateTime.get(), lastUpdateMilli));
//        });
//
//        userSummary.setErrorRates(errorRates);
//        long createTimeMilli = LocalDateTime.parse(profileInfo.get("created_time").toString(), formatter)
//                .toInstant(ZoneOffset.of("-07:00"))
//                .toEpochMilli();
//        userSummary.setLastUpdate(lastUpdateTime.get());
//        userSummary.setCreatedTime(createTimeMilli);
//
//        return userSummary;
//    }
//}