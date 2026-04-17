# Quiz Application (Spring Boot + React)

Full continuous implementation from auth -> quiz management -> attempts -> scoring -> result integration.

## Backend

- **Stack:** Spring Boot, Spring Security (JWT), JPA, MySQL
- **Modules:** Auth, Admin Quiz CRUD, Quiz Attempt, Scoring, Result storage, Email notifications

### Run backend

```bash
cd guviproject2
./mvnw spring-boot:run
```

Backend URL: `http://localhost:8080`

## Frontend

- **Stack:** React (Vite), Tailwind CSS, React Router, Axios
- **Pages:** Auth, Dashboard, Quiz List, Quiz Taking (palette + timer), Result, Admin quiz create

### Run frontend

```bash
cd guviproject2/frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:5173`

## Key API Contracts

### Register

`POST /api/auth/register`

```json
{
  "email": "admin@example.com",
  "password": "password123",
  "role": "ADMIN"
}
```

### Login

`POST /api/auth/login`

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

### Create Quiz (Admin)

`POST /api/admin/quizzes`

```json
{
  "title": "Java Basics",
  "description": "MCQ assessment",
  "durationMinutes": 20,
  "questions": [
    {
      "text": "Which keyword is used for inheritance?",
      "options": [
        { "text": "extends", "correct": true },
        { "text": "implements", "correct": false },
        { "text": "super", "correct": false },
        { "text": "class", "correct": false }
      ]
    }
  ]
}
```

### Submit Attempt

`POST /api/attempts/{quizId}/submit`

```json
{
  "answers": {
    "1": 4,
    "2": 8
  }
}
```

## Config

- Use profile `mysql` in Railway: `SPRING_PROFILES_ACTIVE=mysql`
- If `SPRING_PROFILES_ACTIVE` is not set, the app auto-enables `mysql` profile when Railway MySQL env vars are present.
- Set MySQL env vars in Railway service variables:
  - `MYSQLHOST` (or `RAILWAY_PRIVATE_DOMAIN`)
  - `MYSQLPORT` (default `3306`)
  - `MYSQLDATABASE` (or `MYSQL_DATABASE`)
  - `MYSQLUSER`
  - `MYSQLPASSWORD` (or `MYSQL_ROOT_PASSWORD`)
- Mail notifications are controlled by `APP_MAIL_ENABLED=true` and mail vars (`MAIL_USERNAME`, `MAIL_PASSWORD`, etc.)
# Guvip2_backend
