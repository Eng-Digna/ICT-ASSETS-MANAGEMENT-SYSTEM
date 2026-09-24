ALTER TABLE users
ADD COLUMN station_id BIGINT REFERENCES station (id);