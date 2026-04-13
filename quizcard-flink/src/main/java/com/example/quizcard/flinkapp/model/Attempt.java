package com.example.quizcard.flinkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Attempt implements Serializable {
    String id;
    String accountId;
    List<Question> questions;
    String timestamp;
}
