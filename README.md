## Adaptive Learning Platform

### User Story
This system is meant to support an online learning platform on General Education, a Jeopardy-style platform
Subjects includes: business, law, psychology, biology, chemistry, history, health, economics,
math, physics, computer science, philosophy, engineering, other

With the real-time processing of user's statistics, the platform will recommend the question suite with the latest user profile.

```
Adaptive learning 
An educational method that uses algorithms and AI to automatically 
customize instruction, content, and pacing in real-time. Instead of a traditional, 
one-size-fits-all approach, it acts like a GPS for education, continuously adjusting 
to each learner's performance, strengths, and weaknesses.
```

### High-level Design

AWS + Confluent Kafka + Flink + Spring Boot

```
End User (UI + Kafka) ~60 ms
└─── Drop user.assessment.result events to Kafka Cluster

flinkstreamprocesssvc (Flink + Kafka)
├── Feature Calculation ~50 ms
├── Exactly-once Kafka Producer → [user.assessment.feature topic] ~ 30ms

Confluent Kafka (Kafka + NoSql DB + Data Warehouse)
├─── Sink Conncetor B-online ~30 ms
│    └── Idempotent write → Online Feature Store (Redis/DynamoDB)
│
└─── Sink Conncetor B-offline (consumer group: offline-store) ~5 min
     └── Sink → Offline Feature Store (S3/Parquet/Glue)     
 
 Total End-to-End latency ~ 170ms           
          
 End User (REST API) 
 └─── Submit the assements and Request a new one from Inference Service (interval between operations ~1s)         
```

### Reference
- dataset: MMLU, huggingface "Xueyan/my-qna-dataset"
- subjects (14): business, law, psychology, biology, chemistry, history, health, economics,
  math, physics, computer science, philosophy, engineering, other
- Weighted Random Sampling: P(select topic X) = error_rate(X) / sum(all error_rates), then pick the quiz questions
  from selected topics
- Bayesian Knowledge Tracing (BKT) Tracks P(learned) per skill over timeKhan Academy-style

### Command to use in dev env
#### Server
- KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
- bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties 
- bin/kafka-server-start.sh config/server.properties
- bin/kafka-server-stop.sh 
#### Topics
- bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
- bin/kafka-topics.sh --create --topic quiz-results-topic --bootstrap-server localhost:9092
- bin/kafka-console-consumer.sh --topic quiz-results-topic --from-beginning --bootstrap-server localhost:9092
- bin/kafka-console-producer.sh --topic quiz-results-topic --bootstrap-server localhost:9092
