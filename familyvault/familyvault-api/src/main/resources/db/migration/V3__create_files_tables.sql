-- V3__create_files_tables.sql
-- File storage tables

-- Folders table
CREATE TABLE folders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    parent_id       UUID REFERENCES folders(id) ON DELETE CASCADE,
    created_by      UUID NOT NULL REFERENCES users(id),
    
    name            VARCHAR(255) NOT NULL,
    path            TEXT NOT NULL,
    
    -- Settings
    settings        JSONB DEFAULT '{}',
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    -- Unique path per family
    CONSTRAINT uk_folder_path UNIQUE(family_id, path)
);

-- Indexes
CREATE INDEX idx_folders_family ON folders(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_parent ON folders(parent_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_path ON folders(path) WHERE deleted_at IS NULL;

-- Files table
CREATE TABLE files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    folder_id       UUID REFERENCES folders(id) ON DELETE SET NULL,
    uploaded_by     UUID NOT NULL REFERENCES users(id),
    
    -- File info
    original_name   VARCHAR(500) NOT NULL,
    storage_key     VARCHAR(500) NOT NULL UNIQUE,
    
    -- Type and size
    mime_type       VARCHAR(100) NOT NULL,
    file_size       BIGINT NOT NULL,
    file_type       VARCHAR(20) NOT NULL,
    
    -- Media specific
    thumbnail_key   VARCHAR(500),
    width           INTEGER,
    height          INTEGER,
    duration_seconds INTEGER,
    
    -- Metadata (EXIF, etc.)
    metadata        JSONB DEFAULT '{}',
    
    -- Status
    status          VARCHAR(20) DEFAULT 'PROCESSING',
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_file_type CHECK (file_type IN ('IMAGE', 'VIDEO', 'DOCUMENT', 'AUDIO', 'OTHER')),
    CONSTRAINT chk_status CHECK (status IN ('PROCESSING', 'ACTIVE', 'FAILED'))
);

-- Indexes
CREATE INDEX idx_files_family ON files(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_folder ON files(folder_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_uploaded_by ON files(uploaded_by) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_type ON files(file_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_created ON files(created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_status ON files(status) WHERE deleted_at IS NULL;
