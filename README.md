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

## Config (Railway)

### Recommended backend variables

```env
SPRING_PROFILES_ACTIVE=mysql

MYSQLHOST=mysql.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=<mysql-password>

APP_MAIL_ENABLED=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<sender-email>
MAIL_PASSWORD=<gmail-app-password>
```

### Supported DB input styles

Any one style works:

- Spring style: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- Railway MySQL style: `MYSQLHOST` / `MYSQLPORT` / `MYSQLDATABASE` / `MYSQLUSER` / `MYSQLPASSWORD`
- URL style: `MYSQL_URL` or `MYSQL_PUBLIC_URL`

### Important conflict rule

If you use `MYSQLHOST`/`MYSQLPORT`/`MYSQLDATABASE`, remove or leave empty conflicting URL vars (`MYSQL_URL`, `MYSQL_PUBLIC_URL`) that point to another host.

### Troubleshooting

- Browser shows CORS error + backend returns `502`: backend is down; check Railway deploy logs.
- `Communications link failure` / `Connection refused`: DB host/port is wrong or DB is not reachable from backend service.
- `/api/admin/*` returns `403`: use a valid `ADMIN` token.
