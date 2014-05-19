CREATE SEQUENCE users_seq;

CREATE TABLE users (
  id         BIGINT DEFAULT nextval('users_seq') PRIMARY KEY,
  first_name VARCHAR(256) NOT NULL,
  last_name  VARCHAR(256) NOT NULL,
  sort       BIGINT DEFAULT 1000000
);

INSERT INTO users (first_name, last_name, sort) VALUES ('Frank', 'Zappa', 43);
INSERT INTO users (first_name, last_name, sort) VALUES ('Ike', 'Willis', 11);
INSERT INTO users (first_name, last_name, sort) VALUES ('Tommy', 'Mars', 12);
