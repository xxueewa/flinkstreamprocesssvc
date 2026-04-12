### Start kafka server

- KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
- bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties 
- bin/kafka-server-start.sh config/server.properties

- bin/kafka-server-stop.sh

### Topics
- bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
- bin/kafka-topics.sh --create --topic quiz-results-topic --bootstrap-server localhost:9092
- bin/kafka-console-consumer.sh --topic quiz-results-topic --from-beginning --bootstrap-server localhost:9092
- bin/kafka-console-producer.sh --topic quiz-results-topic --bootstrap-server localhost:9092


### High-level Design
- adaptive learning
- test dataset: MMLU,huggingface "Xueyan/my-qna-dataset"
- subjects (14): business, law, psychology, biology, chemistry, history, health, economics, 
  math, physics, computer science, philosophy, engineering, other
- account profile: accountId, each subjects' error rate
- Weighted Random Sampling: P(select topic X) = error_rate(X) / sum(all error_rates), then pick the quiz questions 
  from selected topics
- Bayesian Knowledge Tracing (BKT) Tracks P(learned) per skill over timeKhan Academy-style


## In progress
- use Table API to handle sql queries?
- change the database & data model?
- methods to sampling the topics and retrieve quiz question set from dataset