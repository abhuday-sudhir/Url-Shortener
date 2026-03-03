# URL Shortener

A production-style URL Shortener built using **Spring Boot**, **PostgreSQL**, and **Redis**, designed with scalability and system design principles in mind.

This project goes beyond basic CRUD by implementing:

* Cache-aside pattern using Redis
* LRU-based eviction strategy
* Batched click aggregation
* Scheduled synchronization to database
* Clean layered architecture

---

## Features

### URL Shortening

* Auto-generates Base62 encoded short codes
* Supports custom aliases
* Prevents duplicate aliases

### High-Performance Redirect

* Cache-first lookup using Redis
* Falls back to PostgreSQL on cache miss
* 24-hour TTL for cached URLs

### Click Analytics (Scalable Design)

* Clicks incremented in Redis (fast, non-blocking)
* Scheduled batch sync to PostgreSQL
* Prevents heavy DB write load under traffic

### Redis Optimization

* LRU eviction policy supported
* Namespaced keys (`url:{shortCode}`, `clicks:{shortCode}`)
* Designed for high-traffic scenarios

---

## Architecture

```
Client
   ↓
Controller
   ↓
Service Layer
   ↓
Redis (Cache + Click Aggregation)
   ↓
PostgreSQL (Persistent Storage)
```

### Redirect Flow

1. Check Redis cache for short code
2. If found → redirect immediately
3. If not found → fetch from DB → cache result
4. Increment click counter in Redis

### Click Sync Flow

1. Scheduled job runs every 60 seconds
2. Reads `clicks:*` keys from Redis
3. Batch updates clickCount in PostgreSQL
4. Deletes synced Redis counters

---

## 🛠 Tech Stack

* **Java 17+**
* **Spring Boot**
* **Spring Data JPA**
* **PostgreSQL**
* **Redis**
* **Maven**

---

## API Endpoints

### Create Short URL

```
POST /shorten
```

**Request Body**

```json
{
  "longURL": "https://example.com",
  "alias": "customAlias" // optional
}
```

**Response**

```
abc123
```

---

### Redirect

```
GET /get/{shortUrl}
```

Redirects to original long URL.

---

## Database Schema

**Table: url_mapping**

| Column     | Type                  |
| ---------- | --------------------- |
| id         | bigint (PK)           |
| longurl    | varchar(255)          |
| shortCode  | varchar(255) (unique) |
| createdAt  | timestamp             |
| expiry     | timestamp (optional)  |
| clickCount | bigint                |

---

# ⚙Running Redis (Terminal Guide)

You can run Redis locally using either **Homebrew (Mac)** or **Docker**.

---

## Option 1: Using Homebrew (Mac)

### Install Redis

```bash
brew install redis
```

### Start Redis

```bash
brew services start redis
```

### Check if Redis is Running

```bash
redis-cli ping
```

If working, it returns:

```
PONG
```

### Open Redis CLI

```bash
redis-cli
```

Inside CLI you can run:

```
KEYS *
GET url:abc123
GET clicks:abc123
```

---

## Option 2: Using Docker (Recommended)

### Run Redis Container

```bash
docker run -d -p 6379:6379 --name redis redis
```

### Access Redis CLI

```bash
docker exec -it redis redis-cli
```

---

## Configure LRU Policy (Optional but Recommended)

Inside Redis CLI:

```
CONFIG SET maxmemory 100mb
CONFIG SET maxmemory-policy allkeys-lru
```

This ensures least recently used keys are automatically evicted when memory is full.

---

# Running the Application

### 1️ .Start PostgreSQL

Ensure your database is created.

### 2️. Configure `application.yaml`

Add your DB and Redis configs locally (not committed to Git).

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/urlShortener
    username: postgres
    password: yourpassword

  redis:
    host: localhost
    port: 6379
```

### 3️. Start Application

```
mvn spring-boot:run
```

---

# Scalability Design Decisions

* **Cache-aside pattern** for fast redirects
* **Redis click aggregation** to avoid DB bottlenecks
* **Batch updates** instead of per-request DB writes
* Prepared for future event-driven architecture (Kafka-ready design)

---

# Future Improvements

* Replace scheduled sync with Kafka event streaming
* Add rate limiting using Redis
* Add trending URLs using Redis Sorted Sets
* Add distributed locking for multi-instance deployment
* Dockerize full stack
* Deploy to cloud (AWS/GCP/Azure)
