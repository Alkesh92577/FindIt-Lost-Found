# FindIt — Lost & Found Platform

A runnable full-stack starter project with React + Vite + Tailwind CSS frontend, Spring Boot REST API, and MySQL database.

## Features
- Public home page and responsive UI
- Browse lost/found listings with search and category filters
- Report a lost or found item
- Item detail view
- Register/login using API-backed demo authentication (JWT)
- Claim requests for listings
- User dashboard with reports and claims
- Admin listing moderation endpoints
- Database schema and Docker Compose for MySQL
- Seed data on first startup

## Requirements
- Node.js 20+
- Java 17+
- Maven 3.9+
- Docker Desktop (recommended), or MySQL 8

## Quick start

### 1. Start MySQL
From the project root:
```bash
docker compose up -d mysql
```
This creates database `findit` on port 3306 with credentials from `.env.example`.

If you already have MySQL running on port 3306, change the host port in `docker-compose.yml` from `3307:3306` (default) or stop your existing MySQL service. This project uses host port **3307** by default to avoid conflicts.

### 2. Start backend
```bash
cd backend
# Windows PowerShell:
$env:DB_URL="jdbc:mysql://localhost:3307/findit?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="findit"
$env:DB_PASSWORD="findit_dev_password"
mvn spring-boot:run
```
Backend: http://localhost:8080
Health: http://localhost:8080/api/health

Seeded demo accounts:
- Admin: `alkesh6420@gmail.com` / `Choudhary`
- User: `demo@findit.local` / `Demo123!`

Change these credentials and JWT secret before any public deployment.

### 3. Start frontend
In another terminal:
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173

The frontend proxies `/api` requests to the backend at localhost:8080.

## API overview
- `GET /api/health`
- `GET /api/items?type=LOST|FOUND&category=...&q=...`
- `GET /api/items/{id}`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/items` (authenticated; multipart form)
- `POST /api/items/{id}/claims` (authenticated)
- `GET /api/items/mine` (authenticated)
- `GET /api/me/claims` (authenticated)
- `GET /api/admin/items` (admin)
- `PATCH /api/admin/items/{id}/status` (admin)

## Security notes before production
This is a starter project, not a fully audited production service. Before launch:
- Replace demo credentials and JWT secret.
- Configure HTTPS, CORS, rate limits, email verification, password reset and monitoring.
- Use private object storage for images; local uploads are for development only.
- Never publicly expose ID numbers, serial numbers, exact home addresses or private contact details.
- Verify ownership before handing over valuables. A similarity match is only a lead, not proof.
- Add abuse reporting, retention rules, backups and privacy policy.

## Private claim chat (updated)

- A claimant submits a claim against a `FOUND` listing.
- The owner of that found listing sees the request in **Dashboard → Incoming claim requests** and can approve/reject it.
- Once approved, both the claimant and the listing owner can open **Open chat** from their dashboard.
- Messages are persisted in the `chat_messages` table. Hibernate `ddl-auto=update` creates this table on startup.
- The API checks that the current user is either the claim's claimant or listing owner and that the claim is `APPROVED` before reading or sending messages. Message DTOs contain sender name/content/time only, not phone numbers.
- Chat refreshes messages every 3 seconds; this is polling, not WebSocket push.

Chat endpoints:
- `GET /api/claims/{claimId}/messages`
- `POST /api/claims/{claimId}/messages` with `{ "content": "Hello" }`
- `GET /api/me/incoming-claims`
- `PATCH /api/claims/{claimId}/decision` with `{ "status": "APPROVED" }` or `{ "status": "REJECTED" }`

Use the existing local MySQL configuration and make sure the database is reachable before starting the backend. Phone-number OTP verification is not included.
