# FamilyVault MVP - Architecture Overview

## Vision
A simple, secure family cloud storage application that can grow into a full-featured family platform.

## MVP Scope (v1.0)

### In Scope
- User authentication (register, login, password reset)
- Family creation and member invitations
- File upload/download (photos, videos, documents)
- Folder organization
- Basic gallery view
- Family-level sharing

### Out of Scope (Future Phases)
- Family tree visualization
- Activity feed / social features
- Chat / messaging
- Calendar / events
- AI-powered features
- Mobile apps

## Architecture Principles

### 1. Clean Architecture
Separate concerns into layers:
- **Domain** - Business logic, entities, use cases (no framework dependencies)
- **Application** - Orchestration, DTOs, ports/interfaces
- **Infrastructure** - Database, storage, external services
- **Presentation** - REST API, controllers

### 2. Hexagonal Architecture (Ports & Adapters)
- Define interfaces (ports) in the domain/application layer
- Implement adapters in infrastructure layer
- Easy to swap implementations (e.g., change storage provider)

### 3. Event-Driven Ready
- Domain events for important actions
- Easy to add notifications, activity feed, analytics later

### 4. API-First Design
- Versioned REST API
- OpenAPI documentation
- Frontend-agnostic (can add mobile apps later)

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Clients                                   │
│    ┌──────────┐    ┌──────────┐    ┌──────────────┐            │
│    │   Web    │    │  Mobile  │    │   Desktop    │            │
│    │  (React) │    │ (Future) │    │   (Future)   │            │
│    └────┬─────┘    └────┬─────┘    └──────┬───────┘            │
└─────────┼───────────────┼─────────────────┼─────────────────────┘
          │               │                 │
          └───────────────┼─────────────────┘
                          │ HTTPS
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway / Load Balancer                   │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Spring Boot Application                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Presentation Layer                       │ │
│  │    REST Controllers │ Exception Handlers │ API Versioning   │ │
│  └────────────────────────────┬───────────────────────────────┘ │
│                               │                                  │
│  ┌────────────────────────────▼───────────────────────────────┐ │
│  │                    Application Layer                        │ │
│  │    Use Cases │ DTOs │ Mappers │ Ports (Interfaces)          │ │
│  └────────────────────────────┬───────────────────────────────┘ │
│                               │                                  │
│  ┌────────────────────────────▼───────────────────────────────┐ │
│  │                      Domain Layer                           │ │
│  │    Entities │ Value Objects │ Domain Services │ Events      │ │
│  └────────────────────────────┬───────────────────────────────┘ │
│                               │                                  │
│  ┌────────────────────────────▼───────────────────────────────┐ │
│  │                   Infrastructure Layer                      │ │
│  │    Repositories │ Storage Adapter │ Email Adapter │ Security│ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────┬─────────────────────────────────┬───────────────────┘
            │                                 │
            ▼                                 ▼
┌───────────────────────┐         ┌───────────────────────────────┐
│      PostgreSQL       │         │      Cloudflare R2            │
│    (Metadata, Users,  │         │    (File Storage)             │
│     Families, etc.)   │         │                               │
└───────────────────────┘         └───────────────────────────────┘
```

## Technology Stack

| Component | Technology | Rationale |
|-----------|------------|-----------|
| Backend | Spring Boot 3.2+ | Familiar, mature, excellent ecosystem |
| Database | PostgreSQL 16 | Reliable, supports JSON, good for future features |
| Storage | Cloudflare R2 | S3-compatible, free egress, cheap |
| Cache | Redis (optional) | Session storage, caching (add when needed) |
| Frontend | React 18 + TypeScript | Modern, large ecosystem, mobile-ready |
| Styling | Tailwind CSS | Rapid development, consistent design |
| Auth | JWT + Refresh Tokens | Stateless, scalable |
| API Docs | SpringDoc OpenAPI | Auto-generated documentation |
| Build | Maven | Standard, reliable |
| Deployment | Docker + GCP Cloud Run | Scalable, cost-effective |

## Module Structure

```
familyvault/
├── familyvault-api/          # REST API module
├── familyvault-core/         # Domain + Application layer
├── familyvault-infrastructure/  # Database, Storage, External services
└── familyvault-web/          # React frontend (separate repo optional)
```

## Key Design Decisions

### 1. Multi-Module Maven Project
Enforces layer separation at compile time. Domain layer cannot accidentally depend on Spring or infrastructure code.

### 2. S3-Compatible Storage Interface
We code against S3 API. Can switch from R2 to AWS S3, Backblaze B2, or MinIO without code changes.

### 3. Presigned URLs for File Operations
- Upload: Client gets presigned URL, uploads directly to R2
- Download: Client gets presigned URL, downloads directly from R2
- Benefits: Reduces server load, cheaper, faster

### 4. Event Sourcing Ready
Domain events are captured for all important actions. Easy to add:
- Activity feed
- Notifications
- Analytics
- Audit logs

### 5. Soft Deletes
All entities use soft delete (deleted_at timestamp). Enables:
- Trash/recycle bin feature
- Data recovery
- Audit compliance

### 6. UUID Primary Keys
Using UUIDs instead of auto-increment:
- No ID enumeration attacks
- Easier distributed systems
- Better for API exposure

## Security Considerations

- JWT tokens with short expiry (15 min) + refresh tokens (7 days)
- Password hashing with BCrypt (strength 12)
- Rate limiting on auth endpoints
- CORS configuration for frontend
- Input validation on all endpoints
- SQL injection prevention (JPA parameterized queries)
- XSS prevention (React auto-escapes)
- HTTPS only in production
