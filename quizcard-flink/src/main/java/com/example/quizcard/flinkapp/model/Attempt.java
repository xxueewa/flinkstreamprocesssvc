package com.example.quizcard.flinkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Attempt implements Serializable {
    String id;
    String accountId;
    Map<String, Double> originalSuccessRate;
    List<Question> questions;
    String timestamp;
}
