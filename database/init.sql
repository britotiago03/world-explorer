-- Schemas
CREATE SCHEMA IF NOT EXISTS country_schema;
CREATE SCHEMA IF NOT EXISTS history_schema;
CREATE SCHEMA IF NOT EXISTS politics_schema;
CREATE SCHEMA IF NOT EXISTS economy_schema;
CREATE SCHEMA IF NOT EXISTS religion_schema;
CREATE SCHEMA IF NOT EXISTS geography_schema;
CREATE SCHEMA IF NOT EXISTS region_schema;
CREATE SCHEMA IF NOT EXISTS climate_schema;
CREATE SCHEMA IF NOT EXISTS event_schema;

-- Main country table
CREATE TABLE IF NOT EXISTS country_schema.countries (
    id SERIAL PRIMARY KEY,
    name TEXT,
    official_name TEXT,
    iso_code TEXT,
    capital TEXT,
    demonym TEXT,
    area DOUBLE PRECISION,
    water_percent DOUBLE PRECISION,
    population BIGINT,
    population_density DOUBLE PRECISION,
    calling_code TEXT,
    internet_tld TEXT,
    date_format TEXT,
    timezone TEXT,
    summer_timezone TEXT,
    currency TEXT,
    flag_url TEXT,
    coat_of_arms_url TEXT
);

-- Native names
CREATE TABLE IF NOT EXISTS country_schema.country_native_names (
    country_id BIGINT NOT NULL,
    native_name TEXT,
    FOREIGN KEY (country_id) REFERENCES country_schema.countries(id) ON DELETE CASCADE
);

-- Official languages
CREATE TABLE IF NOT EXISTS country_schema.country_official_languages (
    country_id BIGINT NOT NULL,
    language TEXT,
    FOREIGN KEY (country_id) REFERENCES country_schema.countries(id) ON DELETE CASCADE
);

-- Recognized languages
CREATE TABLE IF NOT EXISTS country_schema.country_recognized_languages (
    country_id BIGINT NOT NULL,
    language TEXT,
    FOREIGN KEY (country_id) REFERENCES country_schema.countries(id) ON DELETE CASCADE
);

-- Event service tables
CREATE TABLE IF NOT EXISTS event_schema.events (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    data TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR(10) DEFAULT '1.0'
);

-- Indexes for event table for better performance
CREATE INDEX IF NOT EXISTS idx_events_type ON event_schema.events(event_type);
CREATE INDEX IF NOT EXISTS idx_events_source ON event_schema.events(source);
CREATE INDEX IF NOT EXISTS idx_events_timestamp ON event_schema.events(timestamp);
CREATE INDEX IF NOT EXISTS idx_events_entity_id ON event_schema.events(entity_id);