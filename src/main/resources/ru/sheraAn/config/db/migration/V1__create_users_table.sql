CREATE TABLE IF NOT EXISTS users(
    chat_id BIGINT PRIMARY KEY,
    username VARCHAR(255),
    first_name VARCHAR(255),
    city VARCHAR(255),
    registered_at TIMESTAMP NOT NULL
    )
