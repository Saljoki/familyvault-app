-- V2__create_families_tables.sql
-- Family management tables

CREATE TABLE families (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    family_picture  VARCHAR(500),
    
    -- Invite system
    invite_code     VARCHAR(20) NOT NULL UNIQUE,
    invite_enabled  BOOLEAN DEFAULT TRUE,
    
    -- Storage
    storage_limit_bytes BIGINT DEFAULT 10737418240,  -- 10GB default
    storage_used_bytes  BIGINT DEFAULT 0,
    
    -- Settings
    settings        JSONB DEFAULT '{}',
    
    -- Ownership
    created_by      UUID NOT NULL REFERENCES users(id),
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- Indexes
CREATE INDEX idx_families_invite_code ON families(invite_code) WHERE deleted_at IS NULL;
CREATE INDEX idx_families_created_by ON families(created_by) WHERE deleted_at IS NULL;

-- Family members junction table
CREATE TABLE family_members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Role and permissions
    role            VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    
    -- Status
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    invited_by      UUID REFERENCES users(id),
    
    -- Future: relationship info for family tree
    relationship_data JSONB DEFAULT '{}',
    
    -- Audit
    joined_at       TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    -- Constraints
    CONSTRAINT uk_family_member UNIQUE(family_id, user_id),
    CONSTRAINT chk_role CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACTIVE', 'LEFT'))
);

-- Indexes
CREATE INDEX idx_family_members_family ON family_members(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_family_members_user ON family_members(user_id) WHERE deleted_at IS NULL;
