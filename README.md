# AI-Powered Task Management System

A robust, full-stack Task Management System built with **Java 17**, **Spring Boot 3**, and an embedded **H2 database**. It features complete Task CRUD capabilities, a beautiful glassmorphism-themed frontend UI, and seamless integration with the Google Gemini API to automatically generate structured tasks from natural language prompts.

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot 3.2.x, Spring Data JPA, Maven
- **Database:** H2 In-Memory Database
- **Frontend:** Vanilla HTML5, CSS3, JavaScript
- **AI Integration:** Google Gemini API (gemini-flash-latest)

---

## 🚀 Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone <your-repository-url>
   cd eulerity
   ```

2. **Set your API Key:**
   The AI Assistant requires a Google Gemini API Key. You must set this as an environment variable before running the application.

   *On Windows (PowerShell):*
   ```powershell
   $env:GEMINI_API_KEY="your_api_key_here"
   ```
   *On Mac/Linux:*
   ```bash
   export GEMINI_API_KEY="your_api_key_here"
   ```
   > **Note:** If you do not provide an API key, the system will use a fallback mock response so you can still test the UI flow safely.

---

## ▶️ How to Run the Project

You can build, test, and start the entire application locally with a **single command**. 

Run the following command from the project root:

*On Windows:*
```bash
.\mvnw.cmd spring-boot:run
```

*On Mac/Linux:*
```bash
./mvnw spring-boot:run
```

Once the application has started:
- Access the UI at: [http://localhost:8080](http://localhost:8080)
- Access the API at: `http://localhost:8080/tasks`
- Access the H2 Database Console at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, username: `sa`, blank password).

---

## 🤖 AI-Powered Endpoint

The system includes a special AI-powered endpoint that translates casual, plain-language text into a highly structured JSON Task object.

**Endpoint:** `POST /tasks/suggest`

**Description:**
This endpoint accepts a `prompt` string and sends it to the `gemini-flash-latest` model with a strict system instruction to extract the task intent, title, description, due date, priority, and status into a specific JSON schema.

### Example Request

```http
POST /tasks/suggest
Content-Type: application/json

{
  "prompt": "Remind me to submit my deep learning assignment before next monday"
}
```

### Example Response

```json
{
  "id": null,
  "title": "Submit deep learning assignment",
  "description": "Don't forget to submit the deep learning assignment.",
  "dueDate": "2026-05-04",
  "priority": "HIGH",
  "status": "TODO"
}
```

*The frontend UI seamlessly captures this response and auto-populates the Task Creation form for your review before saving it to the database.*

---

## 🧪 Running Tests

The application includes a comprehensive test suite covering Service-layer Unit tests, Controller Integration tests, and AI Service mocks. 

To run the tests:
```bash
./mvnw clean test
```
