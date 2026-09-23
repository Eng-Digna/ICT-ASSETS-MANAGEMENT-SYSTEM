ALTER TABLE assignment ADD COLUMN assignee_name VARCHAR(255);
-- Temporarily set it to 'Unknown' or user's name if they existed
UPDATE assignment SET assignee_name = (SELECT first_name || ' ' || last_name FROM users WHERE users.id = assignment.user_id) WHERE user_id IS NOT NULL;
UPDATE assignment SET assignee_name = 'Unknown' WHERE assignee_name IS NULL;
ALTER TABLE assignment ALTER COLUMN assignee_name SET NOT NULL;

ALTER TABLE assignment DROP COLUMN user_id CASCADE;
