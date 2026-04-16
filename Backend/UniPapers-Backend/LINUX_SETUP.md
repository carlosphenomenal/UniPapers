# UniPapers Backend Setup Guide

## Linux / macOS Edition

---

## What You Will Do

1. Install Prerequisites  
2. Set Up the Database  
3. Configure the App  
4. Seed the Database  
5. Run the Server  

---

## 1. Prerequisites

### 1.1 Java 21 (the project toolchain is set to Java 21 in `build.gradle`)

Check if installed:
```bash
java -version
```

Install (if not installed):

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
```

**macOS**
```bash
brew install openjdk@21
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Verify:
```bash
java -version
```

---

### 1.2 Git

Check:
```bash
git --version
```

Install:

```bash
sudo apt install -y git   # Linux
brew install git          # macOS
```

---

### 1.3 Database (Choose One)

#### Option A — Docker (Recommended)

```bash
sudo apt install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

Verify:
```bash
docker --version
```

---

#### Option B — PostgreSQL

```bash
sudo apt install -y postgresql postgresql-contrib
sudo systemctl start postgresql
```

---

## 2. Clone Project

```bash
git clone https://github.com/carlosphenomenal/UniPapers.git
cd UniPapers/Backend/UniPapers-Backend
```

---

## 3. Database Setup

### Option A — Docker

```bash
docker run --name unipapers-postgres \
  -e POSTGRES_DB=unipapers \
  -e POSTGRES_USER=unipapers_user \
  -e POSTGRES_PASSWORD=change_me \
  -p 5432:5432 \
  -d postgres:16
```

Check:
```bash
docker ps
```

Restart:
```bash
docker start unipapers-postgres
```

---

### Option B — Local PostgreSQL

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE unipapers;
CREATE USER unipapers_user WITH ENCRYPTED PASSWORD 'change_me';
GRANT ALL PRIVILEGES ON DATABASE unipapers TO unipapers_user;
\q
```

---

## 4. Configure Application

### Create config:
```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

`src/main/resources/application.yaml` is ignored by Git (see `.gitignore`), so each developer can keep local secrets there.

Edit:
```bash
nano src/main/resources/application.yaml
```
Or use a text editor of your choice.

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

## 5. Seed Database

Place your seed SQL file in `src/main/resources`.

This guide assumes the file name is `seed.sql`:

- `src/main/resources/seed.sql`

If you use a different name, replace it in the commands below.

### Docker

```bash
docker cp src/main/resources/seed.sql unipapers-postgres:/tmp/seed.sql

docker exec -it unipapers-postgres psql \
  -U unipapers_user -d unipapers -f /tmp/seed.sql
```
### Local PostgreSQL

```bash
psql -h localhost -p 5432 -U unipapers_user -d unipapers -f src/main/resources/seed.sql
```

---

## 6. Run Server

```bash
./gradlew bootRun
```
## 7) Troubleshooting

- **DB connection failed**: verify PostgreSQL is running and `spring.datasource.*` in `application.yaml` is correct.
- **Seed fails with "file not found"**: confirm `src/main/resources/seed.sql` exists and run commands from project root.
- **Docker DB not reachable**: ensure container `unipapers-postgres` is running and port `5432` is published.
- **Port already in use**: change `server.port` in `application.yaml`.
- **Permission issues on Gradle wrapper**:

```bash
chmod +x gradlew
```

Test:
http://localhost:8080/api/files/presign/test
