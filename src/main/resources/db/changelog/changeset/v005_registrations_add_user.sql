ALTER TABLE registrations
ADD COLUMN user_id UUID NOT NULL,
ADD COLUMN registration_type VARCHAR(64) NOT NULL,
ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id);