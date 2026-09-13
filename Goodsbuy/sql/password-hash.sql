-- Run against the existing database before deploying this version.
-- BCrypt requires 60 characters; leave room for a future encoding change.
ALTER TABLE member_user MODIFY COLUMN user_pwd VARCHAR(255) NOT NULL;
