# FamilyVault MVP - Project Structure

## Multi-Module Maven Project

```
familyvault/
├── pom.xml                              # Parent POM
├── docker-compose.yml                   # Local development
├── .env.example                         # Environment template
├── README.md
│
├── familyvault-core/                    # Domain + Application Layer
│   ├── pom.xml
│   └── src/main/java/com/familyvault/core/
│       ├── domain/                      # Pure domain (no framework dependencies)
│       │   ├── model/                   # Entities and Value Objects
│       │   │   ├── user/
│       │   │   │   ├── User.java
│       │   │   │   ├── UserId.java
│       │   │   │   └── Email.java
│       │   │   ├── family/
│       │   │   │   ├── Family.java
│       │   │   │   ├── FamilyId.java
│       │   │   │   ├── FamilyMember.java
│       │   │   │   └── FamilyRole.java
│       │   │   └── file/
│       │   │       ├── StoredFile.java
│       │   │       ├── FileId.java
│       │   │       ├── Folder.java
│       │   │       └── FileType.java
│       │   ├── event/                   # Domain Events
│       │   │   ├── DomainEvent.java
│       │   │   ├── user/
│       │   │   │   ├── UserRegisteredEvent.java
│       │   │   │   └── UserLoggedInEvent.java
│       │   │   ├── family/
│       │   │   │   ├── FamilyCreatedEvent.java
│       │   │   │   └── MemberJoinedEvent.java
│       │   │   └── file/
│       │   │       ├── FileUploadedEvent.java
│       │   │       └── FileDeletedEvent.java
│       │   └── service/                 # Domain Services
│       │       └── StorageCalculator.java
│       │
│       ├── application/                 # Application Layer
│       │   ├── port/                    # Ports (Interfaces)
│       │   │   ├── in/                  # Input ports (Use Cases)
│       │   │   │   ├── auth/
│       │   │   │   │   ├── RegisterUserUseCase.java
│       │   │   │   │   ├── LoginUseCase.java
│       │   │   │   │   └── ResetPasswordUseCase.java
│       │   │   │   ├── family/
│       │   │   │   │   ├── CreateFamilyUseCase.java
│       │   │   │   │   ├── JoinFamilyUseCase.java
│       │   │   │   │   └── GetFamilyUseCase.java
│       │   │   │   └── file/
│       │   │   │       ├── UploadFileUseCase.java
│       │   │   │       ├── DownloadFileUseCase.java
│       │   │   │       └── ListFilesUseCase.java
│       │   │   └── out/                 # Output ports (Driven)
│       │   │       ├── persistence/
│       │   │       │   ├── UserRepository.java
│       │   │       │   ├── FamilyRepository.java
│       │   │       │   └── FileRepository.java
│       │   │       ├── storage/
│       │   │       │   └── FileStoragePort.java
│       │   │       ├── email/
│       │   │       │   └── EmailPort.java
│       │   │       └── event/
│       │   │           └── EventPublisherPort.java
│       │   │
│       │   ├── service/                 # Use Case Implementations
│       │   │   ├── auth/
│       │   │   │   ├── AuthService.java
│       │   │   │   └── TokenService.java
│       │   │   ├── family/
│       │   │   │   └── FamilyService.java
│       │   │   └── file/
│       │   │       └── FileService.java
│       │   │
│       │   ├── dto/                     # Data Transfer Objects
│       │   │   ├── request/
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── CreateFamilyRequest.java
│       │   │   │   └── UploadRequest.java
│       │   │   └── response/
│       │   │       ├── AuthResponse.java
│       │   │       ├── UserResponse.java
│       │   │       ├── FamilyResponse.java
│       │   │       └── FileResponse.java
│       │   │
│       │   └── exception/               # Application Exceptions
│       │       ├── ApplicationException.java
│       │       ├── UserNotFoundException.java
│       │       ├── FamilyNotFoundException.java
│       │       ├── UnauthorizedException.java
│       │       └── StorageQuotaExceededException.java
│
├── familyvault-infrastructure/          # Infrastructure Layer
│   ├── pom.xml
│   └── src/main/java/com/familyvault/infrastructure/
│       ├── persistence/                 # JPA Implementations
│       │   ├── entity/                  # JPA Entities
│       │   │   ├── UserEntity.java
│       │   │   ├── FamilyEntity.java
│       │   │   ├── FamilyMemberEntity.java
│       │   │   ├── FolderEntity.java
│       │   │   └── FileEntity.java
│       │   ├── repository/              # Spring Data Repositories
│       │   │   ├── JpaUserRepository.java
│       │   │   ├── JpaFamilyRepository.java
│       │   │   └── JpaFileRepository.java
│       │   ├── adapter/                 # Port Implementations
│       │   │   ├── UserRepositoryAdapter.java
│       │   │   ├── FamilyRepositoryAdapter.java
│       │   │   └── FileRepositoryAdapter.java
│       │   └── mapper/                  # Entity <-> Domain Mappers
│       │       ├── UserMapper.java
│       │       ├── FamilyMapper.java
│       │       └── FileMapper.java
│       │
│       ├── storage/                     # Cloud Storage
│       │   ├── S3StorageAdapter.java    # R2/S3 implementation
│       │   └── S3Config.java
│       │
│       ├── email/                       # Email Service
│       │   └── SmtpEmailAdapter.java
│       │
│       ├── event/                       # Event Publishing
│       │   └── SpringEventPublisher.java
│       │
│       └── security/                    # Security Infrastructure
│           ├── JwtTokenProvider.java
│           ├── JwtAuthenticationFilter.java
│           └── PasswordEncoder.java
│
├── familyvault-api/                     # Presentation Layer (REST API)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/familyvault/api/
│       │   ├── FamilyVaultApplication.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── UserController.java
│       │   │   ├── FamilyController.java
│       │   │   ├── FileController.java
│       │   │   └── FolderController.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   └── OpenApiConfig.java
│       │   ├── exception/
│       │   │   └── GlobalExceptionHandler.java
│       │   └── filter/
│       │       └── RateLimitFilter.java
│       │
│       └── main/resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-prod.yml
│           └── db/migration/             # Flyway migrations
│               ├── V1__create_users.sql
│               ├── V2__create_families.sql
│               └── V3__create_files.sql
│
└── familyvault-web/                     # React Frontend (separate or submodule)
    ├── package.json
    ├── tsconfig.json
    ├── vite.config.ts
    └── src/
        ├── main.tsx
        ├── App.tsx
        ├── api/                         # API Client
        │   ├── client.ts
        │   ├── auth.ts
        │   ├── family.ts
        │   └── files.ts
        ├── components/
        │   ├── common/
        │   ├── auth/
        │   ├── family/
        │   └── files/
        ├── pages/
        │   ├── LoginPage.tsx
        │   ├── RegisterPage.tsx
        │   ├── DashboardPage.tsx
        │   ├── FamilyPage.tsx
        │   └── GalleryPage.tsx
        ├── hooks/
        ├── context/
        ├── types/
        └── utils/
```

## Module Dependencies

```
familyvault-core        (no external dependencies except validation)
       ▲
       │
familyvault-infrastructure (depends on core, Spring, JPA, S3 SDK)
       ▲
       │
familyvault-api         (depends on core, infrastructure, Spring Web)
```

## Key Architecture Rules

### 1. Core Module Rules
- NO Spring framework dependencies
- NO JPA annotations
- Only Java SE + validation annotations
- Defines interfaces (ports) that infrastructure implements

### 2. Infrastructure Module Rules
- Implements all ports from core
- Contains JPA entities (separate from domain models)
- Handles all external integrations
- Uses mappers to convert between JPA entities and domain models

### 3. API Module Rules
- Thin controllers that delegate to use cases
- Handles HTTP concerns (status codes, headers)
- Global exception handling
- Security configuration

## Package Naming Convention

```
com.familyvault.core.domain.model.*      # Domain entities
com.familyvault.core.domain.event.*      # Domain events
com.familyvault.core.application.port.*  # Interfaces
com.familyvault.core.application.service.*  # Use cases

com.familyvault.infrastructure.persistence.*  # Database
com.familyvault.infrastructure.storage.*      # Cloud storage
com.familyvault.infrastructure.security.*     # Security

com.familyvault.api.controller.*         # REST endpoints
com.familyvault.api.config.*             # Spring config
```
