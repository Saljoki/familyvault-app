# FamilyVault

A secure family cloud storage platform built with Spring Boot and React.

## Features (MVP)

- ✅ User authentication (JWT-based)
- ✅ Family creation and member management
- ✅ File upload/download with presigned URLs
- ✅ Folder organization
- ✅ S3-compatible storage (Cloudflare R2, AWS S3, Backblaze B2, MinIO)

## Architecture

```
familyvault/
├── familyvault-core/           # Domain + Application (no framework dependencies)
├── familyvault-infrastructure/ # Database, Storage, External services
├── familyvault-api/            # REST API (Spring Boot)
└── familyvault-web/            # React Frontend (separate repo)
```

See [Architecture Overview](docs/01-ARCHITECTURE-OVERVIEW.md) for details.

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 20+ (for frontend)

## Quick Start

### 1. Start infrastructure

```bash
docker-compose up -d
```

This starts:
- PostgreSQL (port 5432)
- MinIO (ports 9000, 9001)
- Redis (port 6379)

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env with your settings
```

### 3. Run the application

```bash
# Development
./mvnw spring-boot:run -pl familyvault-api -Dspring-boot.run.profiles=dev

# Or build and run
./mvnw clean package -DskipTests
java -jar familyvault-api/target/familyvault-api-1.0.0-SNAPSHOT.jar
```

### 4. Access the API

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MinIO Console: http://localhost:9001 (minioadmin/minioadmin)

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | familyvault |
| `DB_USER` | Database user | familyvault |
| `DB_PASSWORD` | Database password | familyvault |
| `STORAGE_ENDPOINT` | S3-compatible endpoint | http://localhost:9000 |
| `STORAGE_ACCESS_KEY` | Storage access key | minioadmin |
| `STORAGE_SECRET_KEY` | Storage secret key | minioadmin |
| `STORAGE_BUCKET` | Storage bucket name | familyvault |
| `JWT_SECRET` | JWT signing secret | (change in production!) |

## Production Deployment

### Cloudflare R2 Setup

1. Create an R2 bucket in Cloudflare dashboard
2. Create API tokens with read/write access
3. Configure environment:

```bash
STORAGE_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
STORAGE_ACCESS_KEY=<your-access-key>
STORAGE_SECRET_KEY=<your-secret-key>
STORAGE_BUCKET=familyvault
STORAGE_REGION=auto
```

### GCP Cloud Run Deployment

```bash
# Build container
docker build -t familyvault-api .

# Push to Container Registry
docker tag familyvault-api gcr.io/<project-id>/familyvault-api
docker push gcr.io/<project-id>/familyvault-api

# Deploy to Cloud Run
gcloud run deploy familyvault-api \
  --image gcr.io/<project-id>/familyvault-api \
  --platform managed \
  --region <region> \
  --set-env-vars "DB_HOST=..." \
  --allow-unauthenticated
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout

### Families
- `POST /api/v1/families` - Create family
- `GET /api/v1/families` - List user's families
- `GET /api/v1/families/{id}` - Get family details
- `POST /api/v1/families/join` - Join family by invite code
- `POST /api/v1/families/{id}/invite` - Generate invite

### Files
- `POST /api/v1/files/upload/initiate` - Get upload URL
- `POST /api/v1/files/upload/{id}/confirm` - Confirm upload
- `GET /api/v1/files` - List files
- `GET /api/v1/files/{id}/download-url` - Get download URL
- `DELETE /api/v1/files/{id}` - Delete file

## Development

### Running Tests

```bash
# All tests
./mvnw test

# Specific module
./mvnw test -pl familyvault-core
```

### Code Style

The project uses:
- Google Java Style Guide
- Lombok for boilerplate reduction
- MapStruct for DTO mapping

## Roadmap

### Phase 2: Activity Feed
- Real-time updates via WebSocket
- Like/comment on files
- Notification system

### Phase 3: Family Tree
- Interactive visualization
- Relationship management

### Phase 4: Mobile Apps
- React Native iOS/Android apps
- Push notifications

## License

MIT
