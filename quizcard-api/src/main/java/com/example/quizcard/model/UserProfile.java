package com.example.quizcard.model;

import lombok.Data;

import java.util.Map;

@Data
public class UserProfile {
    String accountId;
    String name;
    String email;
    Long createdTime;
}