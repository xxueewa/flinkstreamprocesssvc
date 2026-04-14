package com.example.quizcard.flinkapp.source;

import com.example.quizcard.flinkapp.model.Attempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EventRecordDeserializerTest {

    private EventRecordDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new EventRecordDeserializer();
    }

    @Test
    void deserialize_validPayload_returnsAttempt() throws IOException {
        String json = """
                {
                    "id": "attempt-1",
                    "accountId": "account-42",
                    "timestamp": "2026-04-13T10:00:00Z",
                    "originalSuccessRate": {
                      "chemistry": 0,
                      "other": 0,
                      "biology": 0,
                      "law": 0,
                      "business": 0,
                      "health": 0,
                      "engineering": 0,
                      "history": 0,
                      "philosophy": 0,
                      "economics": 0,
                      "psychology": 0,
                      "computer_science": 0,
                      "physics": 0,
                      "math": 0
                    },
                    "questions": [
                        {
                          "id": 0,
                          "sourceId": 10,
                          "subject": "Math",
                          "level": "easy",
                          "text": "What is 2+2?",
                          "studentAnswer": "4",
                          "answerKey": "4"
                        },
                        {
                          "id": 1,
                          "sourceId": 10,
                          "subject": "Math",
                          "level": "easy",
                          "text": "What is 2+2?",
                          "studentAnswer": "4",
                          "answerKey": "4"
                        }
                    ]
                }
                """;

        Attempt result = deserializer.deserialize(json.getBytes(StandardCharsets.UTF_8));

        assertEquals("attempt-1", result.getId());
        assertEquals("account-42", result.getAccountId());
        assertEquals("2026-04-13T10:00:00Z", result.getTimestamp());
        assertNotNull(result.getQuestions());
        assertEquals(2, result.getQuestions().size());

        assertEquals(14, result.getOriginalSuccessRate().size());

        var question = result.getQuestions().getFirst();
        assertEquals(0, question.getId());
        assertEquals(10, question.getSourceId());
        assertEquals("Math", question.getSubject());
        assertEquals("easy", question.getLevel());
        assertEquals("What is 2+2?", question.getText());
        assertEquals("4", question.getStudentAnswer());
        assertEquals("4", question.getAnswerKey());
    }

    @Test
    void deserialize_emptyQuestions_returnsAttemptWithEmptyList() throws IOException {
        String json = """
                {
                    "id": "attempt-2",
                    "accountId": "account-7",
                     "timestamp": "2026-04-13T11:00:00Z",
                     "originalSuccessRate": {
                          "chemistry": 0,
                          "other": 0,
                          "biology": 0,
                          "law": 0,
                          "business": 0,
                          "health": 0,
                          "engineering": 0,
                          "history": 0,
                          "philosophy": 0,
                          "economics": 0,
                          "psychology": 0,
                          "computer_science": 0,
                          "physics": 0,
                          "math": 0
                        },
                    "questions": []
                }
                """;

        Attempt result = deserializer.deserialize(json.getBytes(StandardCharsets.UTF_8));

        assertEquals("attempt-2", result.getId());
        assertNotNull(result.getQuestions());
        assertTrue(result.getQuestions().isEmpty());
    }

    @Test
    void deserialize_missingOptionalFields_returnsAttemptWithNulls() throws IOException {
        String json = """
                {
                  "id": "attempt-3"
                }
                """;

        Attempt result = deserializer.deserialize(json.getBytes(StandardCharsets.UTF_8));

        assertEquals("attempt-3", result.getId());
        assertNull(result.getAccountId());
        assertNull(result.getTimestamp());
        assertNull(result.getQuestions());
    }

    @Test
    void deserialize_invalidJson_throwsIOException() {
        byte[] invalidBytes = "not-json".getBytes(StandardCharsets.UTF_8);
        assertThrows(IOException.class, () -> deserializer.deserialize(invalidBytes));
    }

    @Test
    void isEndOfStream_alwaysReturnsFalse() throws IOException {
        String json = """
                {"id": "attempt-1"}
                """;
        Attempt attempt = deserializer.deserialize(json.getBytes(StandardCharsets.UTF_8));
        assertFalse(deserializer.isEndOfStream(attempt));
    }

    @Test
    void getProducedType_returnsAttemptType() {
        assertEquals(Attempt.class, deserializer.getProducedType().getTypeClass());
    }
}