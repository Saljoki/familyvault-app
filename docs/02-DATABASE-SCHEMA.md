# FamilyVault MVP - Database Schema

## Design Principles

1. **UUID Primary Keys** - Security and distribution friendly
2. **Soft Deletes** - All tables have `deleted_at` column
3. **Audit Columns** - `created_at`, `updated_at` on all tables
4. **Normalized** - Proper normalization with strategic denormalization for performance
5. **Extensible** - JSON columns for flexible metadata

## Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     users       │       │    families     │       │  family_members │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)         │
│ email           │       │ name            │       │ family_id (FK)  │
│ password_hash   │       │ description     │       │ user_id (FK)    │
│ first_name      │       │ invite_code     │       │ role            │
│ last_name       │◄──────│ created_by (FK) │       │ joined_at       │
│ ...             │       │ ...             │       │ ...             │
└────────┬────────┘       └────────┬────────┘       └─────────────────┘
         │                         │                         ▲
         │                         │                         │
         │                         └─────────────────────────┘
         │
         │        ┌─────────────────┐       ┌─────────────────┐
         │        │    folders      │       │     files       │
         │        ├─────────────────┤       ├─────────────────┤
         │        │ id (PK)         │       │ id (PK)         │
         │        │ family_id (FK)  │       │ folder_id (FK)  │
         └───────►│ created_by (FK) │◄──────│ family_id (FK)  │
                  │ parent_id (FK)  │       │ uploaded_by(FK) │
                  │ name            │       │ storage_key     │
                  │ ...             │       │ ...             │
                  └─────────────────┘       └─────────────────┘
                                                    │
                                                    ▼
                                           ┌─────────────────┐
                                           │  file_shares    │
                                           ├─────────────────┤
                                           │ id (PK)         │
                                           │ file_id (FK)    │
                                           │ shared_with(FK) │
                                           │ permission      │
                                           │ ...             │
                                           └─────────────────┘
```

## Table Definitions

### users
Core user table with authentication and profile information.

```sql
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(500),
    
    -- Account status
    email_verified  BOOLEAN DEFAULT FALSE,
    account_status  VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, SUSPENDED, DELETED
    
    -- Security
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until    TIMESTAMP,
    last_login_at   TIMESTAMP,
    
    -- Future extensibility
    settings        JSONB DEFAULT '{}',
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    CONSTRAINT chk_account_status CHECK (account_status IN ('ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_account_status ON users(account_status) WHERE deleted_at IS NULL;
```

### refresh_tokens
Stores refresh tokens for JWT authentication.

```sql
CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    device_info     VARCHAR(500),
    ip_address      VARCHAR(45),
    expires_at      TIMESTAMP NOT NULL,
    revoked_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);
```

### password_reset_tokens
Temporary tokens for password reset flow.

```sql
CREATE TABLE password_reset_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMP NOT NULL,
    used_at         TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_password_reset_tokens_hash ON password_reset_tokens(token_hash);
```

### families
Family groups that users can create and join.

```sql
CREATE TABLE families (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    family_picture  VARCHAR(500),
    
    -- Invite system
    invite_code     VARCHAR(20) NOT NULL UNIQUE,
    invite_enabled  BOOLEAN DEFAULT TRUE,
    
    -- Settings
    storage_limit_bytes BIGINT DEFAULT 10737418240,  -- 10GB default
    settings        JSONB DEFAULT '{}',
    
    -- Ownership
    created_by      UUID NOT NULL REFERENCES users(id),
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

CREATE INDEX idx_families_invite_code ON families(invite_code) WHERE deleted_at IS NULL;
CREATE INDEX idx_families_created_by ON families(created_by) WHERE deleted_at IS NULL;
```

### family_members
Junction table connecting users to families with roles.

```sql
CREATE TABLE family_members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id),
    user_id         UUID NOT NULL REFERENCES users(id),
    
    -- Role and permissions
    role            VARCHAR(20) NOT NULL DEFAULT 'MEMBER',  -- OWNER, ADMIN, MEMBER
    
    -- Status
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- PENDING, ACTIVE, LEFT
    invited_by      UUID REFERENCES users(id),
    
    -- Future: relationship info for family tree
    relationship_data JSONB DEFAULT '{}',
    
    -- Audit
    joined_at       TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    CONSTRAINT uk_family_member UNIQUE(family_id, user_id),
    CONSTRAINT chk_role CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACTIVE', 'LEFT'))
);

CREATE INDEX idx_family_members_family ON family_members(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_family_members_user ON family_members(user_id) WHERE deleted_at IS NULL;
```

### folders
Folder structure for organizing files.

```sql
CREATE TABLE folders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id),
    parent_id       UUID REFERENCES folders(id),
    created_by      UUID NOT NULL REFERENCES users(id),
    
    name            VARCHAR(255) NOT NULL,
    path            TEXT NOT NULL,  -- Materialized path: /root/photos/2024
    
    -- Future: folder-specific settings
    settings        JSONB DEFAULT '{}',
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    CONSTRAINT uk_folder_path UNIQUE(family_id, path)
);

CREATE INDEX idx_folders_family ON folders(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_parent ON folders(parent_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_path ON folders(path) WHERE deleted_at IS NULL;
```

### files
File metadata (actual files stored in R2/S3).

```sql
CREATE TABLE files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID NOT NULL REFERENCES families(id),
    folder_id       UUID REFERENCES folders(id),
    uploaded_by     UUID NOT NULL REFERENCES users(id),
    
    -- File info
    original_name   VARCHAR(500) NOT NULL,
    storage_key     VARCHAR(500) NOT NULL UNIQUE,  -- Key in R2/S3
    
    -- Type and size
    mime_type       VARCHAR(100) NOT NULL,
    file_size       BIGINT NOT NULL,
    file_type       VARCHAR(20) NOT NULL,  -- IMAGE, VIDEO, DOCUMENT, AUDIO, OTHER
    
    -- Media specific
    thumbnail_key   VARCHAR(500),
    width           INTEGER,
    height          INTEGER,
    duration_seconds INTEGER,
    
    -- Metadata
    metadata        JSONB DEFAULT '{}',  -- EXIF, etc.
    
    -- Status
    status          VARCHAR(20) DEFAULT 'ACTIVE',  -- PROCESSING, ACTIVE, FAILED
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    CONSTRAINT chk_file_type CHECK (file_type IN ('IMAGE', 'VIDEO', 'DOCUMENT', 'AUDIO', 'OTHER')),
    CONSTRAINT chk_status CHECK (status IN ('PROCESSING', 'ACTIVE', 'FAILED'))
);

CREATE INDEX idx_files_family ON files(family_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_folder ON files(folder_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_uploaded_by ON files(uploaded_by) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_type ON files(file_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_created ON files(created_at DESC) WHERE deleted_at IS NULL;
```

### file_shares (Future use)
For sharing files with specific family members.

```sql
CREATE TABLE file_shares (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_id         UUID NOT NULL REFERENCES files(id),
    shared_by       UUID NOT NULL REFERENCES users(id),
    shared_with     UUID REFERENCES users(id),  -- NULL = shared with entire family
    
    permission      VARCHAR(20) NOT NULL DEFAULT 'VIEW',  -- VIEW, DOWNLOAD, EDIT
    
    -- External sharing (future)
    share_token     VARCHAR(100) UNIQUE,
    expires_at      TIMESTAMP,
    
    -- Audit
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    
    CONSTRAINT chk_permission CHECK (permission IN ('VIEW', 'DOWNLOAD', 'EDIT'))
);

CREATE INDEX idx_file_shares_file ON file_shares(file_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_file_shares_user ON file_shares(shared_with) WHERE deleted_at IS NULL;
```

### domain_events (For future activity feed, notifications)
Stores domain events for async processing.

```sql
CREATE TABLE domain_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type      VARCHAR(100) NOT NULL,
    aggregate_type  VARCHAR(50) NOT NULL,
    aggregate_id    UUID NOT NULL,
    actor_id        UUID REFERENCES users(id),
    family_id       UUID REFERENCES families(id),
    
    payload         JSONB NOT NULL,
    
    -- Processing status
    processed       BOOLEAN DEFAULT FALSE,
    processed_at    TIMESTAMP,
    
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_domain_events_unprocessed ON domain_events(created_at) WHERE processed = FALSE;
CREATE INDEX idx_domain_events_family ON domain_events(family_id, created_at DESC);
CREATE INDEX idx_domain_events_type ON domain_events(event_type, created_at DESC);
```

## Migration Strategy

Use Flyway for database migrations:

```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_families_tables.sql
├── V3__create_files_tables.sql
├── V4__create_domain_events_table.sql
└── ...
```

## Future Schema Extensions

When adding new features, these tables can be added:

### Activity Feed (Phase 2)
```sql
-- activities: Denormalized activity feed
-- activity_reactions: Likes, etc.
-- activity_comments: Comments on activities
```

### Family Tree (Phase 3)
```sql
-- family_relationships: Detailed relationship mapping
-- relationship_types: Custom relationship definitions
```

### Messaging (Phase 4)
```sql
-- conversations: Chat threads
-- messages: Individual messages
-- message_attachments: Files attached to messages
```
