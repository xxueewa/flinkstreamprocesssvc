### Start kafka server

- KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
- bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties 
- bin/kafka-server-start.sh config/server.properties


### Topics
- bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
- bin/kafka-topics.sh --create --topic quiz-results-topic --bootstrap-server localhost:9092
- bin/kafka-console-consumer.sh --topic quiz-results-topic --from-beginning --bootstrap-server localhost:9092
- bin/kafka-console-producer.sh --topic quiz-results-topic --bootstrap-server localhost:9092