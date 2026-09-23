-- Hibernate may have created this constraint from an earlier RoleName enum.
-- Keep the database contract aligned with the two application roles.
ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;
ALTER TABLE roles
    ADD CONSTRAINT roles_name_check
    CHECK (name IN ('REGISTRAR', 'ADMINISTRATOR'));
