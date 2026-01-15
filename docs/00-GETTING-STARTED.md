# FamilyVault MVP - Getting Started

## What's Been Created

This architecture gives you a **production-ready foundation** that's simple to start with but designed to scale.

### Project Structure

```
familyvault-architecture/
├── docs/                           # Architecture documentation
│   ├── 01-ARCHITECTURE-OVERVIEW.md # High-level design
│   ├── 02-DATABASE-SCHEMA.md       # Database design
│   └── 03-PROJECT-STRUCTURE.md     # Code organization
│
└── familyvault/                    # The actual project
    ├── pom.xml                     # Parent Maven POM
    ├── docker-compose.yml          # Local dev infrastructure
    ├── .env.example                # Environment template
    ├── README.md                   # Project documentation
    │
    ├── familyvault-core/           # Domain layer (clean)
    │   └── src/main/java/
    │       └── com/familyvault/core/
    │           ├── domain/model/   # User, Family, StoredFile entities
    │           ├── domain/event/   # Domain events
    │           ├── application/port/   # Interfaces (ports)
    │           ├── application/service/  # Use case implementations
    │           ├── application/dto/      # Request/Response DTOs
    │           └── application/exception/  # Business exceptions
    │
    ├── familyvault-infrastructure/ # Infrastructure layer
    │   └── src/main/java/
    │       └── com/familyvault/infrastructure/
    │           ├── persistence/entity/  # JPA entities
    │           └── storage/             # S3/R2 adapter
    │
    └── familyvault-api/            # API layer
        └── src/
            ├── main/java/
            │   └── com/familyvault/api/
            │       ├── controller/      # REST controllers
            │       └── security/        # Auth helpers
            └── main/resources/
                └── application.yml      # Configuration
```

## Key Design Decisions

### 1. Clean Architecture
- **Core module has ZERO framework dependencies**
- Domain entities are separate from JPA entities
- Easy to test, easy to change infrastructure

### 2. S3-Compatible Storage
- Works with Cloudflare R2, AWS S3, Backblaze B2, MinIO
- Uses presigned URLs for direct client uploads
- Zero egress costs with R2

### 3. Event-Ready
- Domain events captured for all actions
- Easy to add activity feed, notifications later

### 4. Soft Deletes + Audit
- All tables have `deleted_at` column
- Full audit trail with `created_at`, `updated_at`

## What You Need to Complete

### Must Have (Core MVP)
1. **JPA Repository implementations** - Wire up the ports to Spring Data
2. **User mapper** - Convert between domain User and JPA UserEntity
3. **Auth endpoints** - Login, register, token refresh
4. **Security configuration** - JWT filter, password encoder
5. **Database migrations** - Flyway SQL files

### Nice to Have
6. **Family endpoints** - Create, join, invite
7. **Folder management** - Create, list, delete folders
8. **Thumbnail generation** - Async image processing
9. **React frontend** - Upload UI, gallery view

## Estimated Effort

| Task | Time |
|------|------|
| Complete backend MVP | 2-3 weekends |
| Basic React frontend | 1-2 weekends |
| Deploy to GCP | 1 day |
| **Total** | **4-6 weekends** |

## Cost Projection (Your Family)

Assuming 500GB storage, light usage:

| Service | Monthly Cost |
|---------|-------------|
| Cloudflare R2 (500GB) | ~$7.50 |
| Cloud Run (light usage) | ~$5-10 |
| Cloud SQL PostgreSQL | ~$7 (or use free tier) |
| **Total** | **~$15-25/month** |

Compared to your current Google One (200GB for ~$23/year), you're paying more BUT getting:
- 2.5x the storage
- Full control over your data
- A fun project to build
- Skills for your portfolio

## Next Steps

1. Copy the `familyvault/` folder to your local machine
2. Run `docker-compose up -d` to start PostgreSQL and MinIO
3. Complete the remaining implementations
4. Ask me to help with any specific piece!
