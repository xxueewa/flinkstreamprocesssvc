-- runs automatically on first container start: docker compose up -d
-- Start PostgresSQL (runs in background)
-- docker compose up -d
--
-- Stop (data is preserved in volume)
-- docker compose down
--
-- Stop AND delete all data (fresh start)
-- docker compose down -v
--
-- Check if running
-- docker compose ps
--
-- View logs
-- docker compose logs postgres

-- docker exec -it quizcard-flink-postgres psql -U myuser -d quizcard_flink_db
--
-- Then run SQL
-- \dt              -- list tables
-- SELECT * FROM user_error_rate;
-- \q               -- quit
-- This design ensures the add/remove of subject won't need to alter the table DDL

CREATE TABLE user_error_rate (
  account_id   VARCHAR(255) NOT NULL REFERENCES user_profile(account_id),
  subject      VARCHAR(255) NOT NULL,
  error_rate   DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  last_update  TIMESTAMP NOT NULL DEFAULT NOW(),
  created_time TIMESTAMP NOT NULL,
  PRIMARY KEY (account_id, subject)
);