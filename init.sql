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

-- docker exec -it flink-postgres psql -U myuser -d mydb
--
-- Then run SQL
-- \dt              -- list tables
-- SELECT * FROM events;
-- \q               -- quit
CREATE TABLE IF NOT EXISTS quiz_log (
    id          SERIAL PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    questions       JSONB,
    timestamp   TIMESTAMP
);

