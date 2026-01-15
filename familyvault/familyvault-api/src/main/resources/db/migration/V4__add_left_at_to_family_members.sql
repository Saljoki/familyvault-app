-- Add left_at column to family_members table to track when members leave
ALTER TABLE family_members ADD COLUMN IF NOT EXISTS left_at TIMESTAMP;
