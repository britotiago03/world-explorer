CREATE TABLE countries (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  iso_code CHAR(2) NOT NULL,
  capital VARCHAR(100),
  population BIGINT,
  area FLOAT
);

CREATE TABLE regions (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  slug VARCHAR(100) UNIQUE,
  capital VARCHAR(100),
  population BIGINT,
  area FLOAT,
  country_id INT REFERENCES countries(id)
);

CREATE TABLE cities (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  region_id INT REFERENCES regions(id),
  population BIGINT,
  latitude DECIMAL,
  longitude DECIMAL
);
