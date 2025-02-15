ALTER TABLE registrations RENAME COLUMN confirmed TO confirm_status;
ALTER TABLE registrations ALTER COLUMN confirm_status TYPE VARCHAR(32) USING confirm_status::VARCHAR(32);