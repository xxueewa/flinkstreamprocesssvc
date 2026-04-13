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

-- docker exec -it quizcard-api-postgres psql -U myuser -d quizcard_api_db
--
-- Then run SQL
-- \dt              -- list tables
-- SELECT * FROM user_profile;
-- \q               -- quit

CREATE TABLE user_profile (
  account_id   VARCHAR(255) PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  email        VARCHAR(255) NOT NULL,
  created_time TIMESTAMP NOT NULL DEFAULT NOW()
);