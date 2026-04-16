# UniPapers Backend Setup Guide

## Windows Edition

---

## What You Will Do

1. Install Prerequisites  
2. Set Up Database  
3. Configure App  
4. Seed Database  
5. Run Server  

---

## 1. Prerequisites

### 1.1 Java 21 (the project toolchain is set to Java 21 in `build.gradle`)

Check:
```powershell
java -version
```

Install (if not installed) from:
https://adoptium.net

---

### 1.2 Git

Install:
https://git-scm.com/download/win

Verify:
```powershell
git --version
```

---

### 1.3 Database

#### Option A — Docker (if you are going to user the docker postgres image)

Install Docker Desktop if not already installed:
https://www.docker.com/products/docker-desktop

Verify:
```powershell
docker --version
docker ps
```

---

#### Option B — PostgreSQL

Download:
https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

---

## 2. Clone Project (skip if you already have the project on your machine)

```powershell
cd $HOME\Documents
git clone https://github.com/carlosphenomenal/UniPapers.git
cd UniPapers\Backend\UniPapers-Backend
```

---

## 3. Database Setup

### Docker

```powershell
docker run --name unipapers-postgres `
  -e POSTGRES_DB=unipapers `
  -e POSTGRES_USER=unipapers_user `
  -e POSTGRES_PASSWORD=change_me `
  -p 5432:5432 `
  -d postgres:16
```

---

### Local PostgreSQL

```powershell
psql -U postgres -h localhost
```

```sql
CREATE DATABASE unipapers;
CREATE USER unipapers_user WITH ENCRYPTED PASSWORD 'change_me';
GRANT ALL PRIVILEGES ON DATABASE unipapers TO unipapers_user;
\q
```

---

## 4. Configure Application

```powershell
Copy-Item src\main\resources\application-example.yaml src\main\resources\application.yaml
```
`src/main/resources/application.yaml` is ignored by Git (see `.gitignore`), so each developer can keep local secrets there.

Open `src/main/resources/application.yaml` in your code editor or IDE and fill every value for your environment. You can skip the pre-filled values in the file.

### 4.1 `server`

- `server.port` (example: `8080`)
- `server.servlet.context-path` (example: `/api`)
- `server.address` (example: `0.0.0.0`)

### 4.2 `spring.application`

- `spring.application.name` (example: `UniPapers-Backend`)

### 4.3 `spring.datasource`

- `spring.datasource.url` (example: `jdbc:postgresql://localhost:5432/unipapers`)
- `spring.datasource.username` (example: `unipapers_user`)
- `spring.datasource.password` (example: `change_me`)
- `spring.datasource.driver-class-name` (use `org.postgresql.Driver`)

### 4.4 `spring.jpa`

- `spring.jpa.hibernate.ddl-auto` (example: `update` for local dev)
- `spring.jpa.show-sql` (example: `false`)

### 4.5 `cloudflare.r2`

Fill all Cloudflare R2 values:

- `cloudflare.r2.account-id`
- `cloudflare.r2.access-key`
- `cloudflare.r2.secret-key`
- `cloudflare.r2.bucket-name`
- `cloudflare.r2.public-url`

> Security note: keep real credentials only in your local `application.yaml`.

---

## 5. Seed Database (from `src/main/resources`)

Place your seed SQL file in `src/main/resources`.

This guide assumes the file name is `seed.sql`:

- `src/main/resources/seed.sql`

If you use a different name, replace it in the commands below.

### Docker

```powershell
docker cp src\main\resources\seed.sql unipapers-postgres:/tmp/seed.sql

docker exec -it unipapers-postgres psql `
  -U unipapers_user -d unipapers -f /tmp/seed.sql
```

### Local PostgreSQL

```powershell
psql -h localhost -p 5432 -U unipapers_user -d unipapers -f src\\main\\resources\\seed.sql
```

---

## 6. Run Server

```powershell
.\gradlew.bat bootRun
```
## 7) Troubleshooting

- **DB connection failed**: verify PostgreSQL is running and `spring.datasource.*` in `application.yaml` is correct.
- **Seed fails with "file not found"**: confirm `src/main/resources/seed.sql` exists and run commands from project root.
- **Docker DB not reachable**: ensure container `unipapers-postgres` is running and port `5432` is published.
- **Port already in use**: change `server.port` in `application.yaml`.
- **Permission issues on Gradle wrapper**:

Test:
http://localhost:8080/api/files/presign/test
