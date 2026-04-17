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

- Update MySQL and SMTP values in `src/main/resources/application.properties`
- Mail notifications are controlled by `app.mail.enabled` (default `false` for local run)
# Guvip2_backend
