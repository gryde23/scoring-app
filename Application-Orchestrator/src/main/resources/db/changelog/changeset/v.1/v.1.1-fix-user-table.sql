ALTER TABLE users DROP CONSTRAINT users_check;

ALTER TABLE users ALTER COLUMN phone SET NOT NULL;

ALTER TABLE users DROP COLUMN email;

ALTER TABLE users ADD password varchar;
