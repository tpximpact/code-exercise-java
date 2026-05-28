# URL Shortener Coding Exercise

## Task

Build a simple **URL shortener** in **your preferred language** (e.g. Java, C#, Python).

It should:

- Accept a full URL and return a shortened URL.
- A shortened URL should have a randomly generated alias.
- Allow a user to **customise the shortened URL** if they want to (e.g. user provides `my-custom-alias` instead of a random string).
- Persist the shortened URLs across restarts.
- Expose a **decoupled web frontend** built with a modern framework (e.g., React, Next.js, Vue.js, Angular, Flask with templates). This can be lightweight form/output just to demonstrate interaction with the API. Feel free to use UI frameworks like Bootstrap, Material-UI, Tailwind CSS, GOV.UK design system, etc. to speed up development.
- Expose a **RESTful API** to perform create/read/delete operations on URLs.  
  → Refer to the provided [`openapi.yaml`](./openapi.yaml) for API structure and expected behaviour.
- Include the ability to **delete a shortened URL** via the API.
- **Have tests**.
- Be containerised (e.g. Docker).
- Include instructions for running locally.

## Rules

- Fork the repository and work in your fork. Do not push directly to the main repository.
- There is no time limit, we want to see something you are proud of. We would like to understand roughly how long you spent on it though.
- **Commit often with meaningful messages.**
- Write tests.
- The API should validate inputs and handle errors gracefully.
- The Frontend should show errors from the API appropriately.
- Use the provided [`openapi.yaml`](./openapi.yaml) as the API contract.
- Focus on clean, maintainable code.
- AI tools (e.g., GitHub Copilot, ChatGPT) are allowed, but please **do not** copy-paste large chunks of code. Use them as assistants, not as a replacement for your own work. We will be asking.

## Deliverables

- Working software.
- Decoupled web frontend (using a modern framework like React, Next.js, Vue.js, Angular, or Flask with templates).
- RESTful API matching the OpenAPI spec.
- Tests.
- A git commit history that shows your thought process.
- Dockerfile.
- README with:
  - How to build and run locally.
  - Example usage (frontend and API).
  - Any notes or assumptions.
-----------------------------------------------------------
-------------------------------------------------------------
# URL Shortener API (Spring Boot)

A RESTful API for shortening URLs, retrieving them, redirecting, and deleting entries.

---

## 🚀 Features

* Create short URLs (with optional custom alias)
* Redirect to original URL
* List all shortened URLs
* Delete URLs
* H2 (dev/test)
* Integration & unit tests
* Docker support

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot 3.5
* Spring Web (REST APIs)
* Spring Data JPA
* H2 Filebased mode
* Maven
* JaCoCo (code coverage)

---

## 📦 Prerequisites

* Java 17+
* Maven (or use Maven Wrapper `./mvnw`)
* Node.js (if running frontend)
* Docker (optional)

---

## ⚙️ Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd url-shortener
```

---

### 2. Build the project

```bash
./mvnw clean install
```

---

### 3. Run the application
Set SpringBoot Profile as an environment variable

```bash
export SPRING_PROFILES_ACTIVE=dev
````

```bash
./mvnw spring-boot:run
```
or
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

### 4. Access API

```text
http://localhost:8080
```

---

## 🔗 API Endpoints

### ➕ Shorten URL

```http
POST /shorten
```

**Request:**

```json
{
  "fullUrl": "https://example.com",
  "customAlias": "my-alias"
}
```

**Response (201):**

```json
{
  "shortUrl": "http://localhost:8080/my-alias"
}
```

---

### 🔁 Redirect to Original URL

```http
GET /{alias}
```

**Response:**

* `302 Found` → Redirects to original URL
* `404 Not Found` → Alias does not exist

---

### 📋 Get All URLs

```http
GET /urls
```

**Response:**

```json
[
  {
    "alias": "my-alias",
    "fullUrl": "https://example.com",
    "shortUrl": "http://localhost:8080/my-alias"
  }
]
```

---

### ❌ Delete URL

```http
DELETE /{alias}
```

**Response:**

* `204 No Content` → Successfully deleted
* `404 Not Found` → Alias not found

---
Access H2 console:

```text
http://localhost:8080/h2-console

```

## ⚙️ Profiles

Run with different profiles:

```bash
java -jar app.jar --spring.profiles.active=dev
```

Available profiles:

* `dev`
* `test`
---

## 🧪 Running Tests

### Unit tests

```bash
./mvnw test
```

### Integration tests

```bash
./mvnw verify
```

---

## 📊 Code Coverage (JaCoCo)

Generate report:

```bash
./mvnw clean verify
```

Open report:

```text
target/site/jacoco/index.html
```
---

## 🐳 Docker

### Build image

```bash
docker build -t url-shortener .
```

### Run the Container by passing spring profile dev

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  url-shortener
```
Note: I'm not pusing docker image to DockerHub. Please build image from the Dockerfile and create a container from the image
## ⚠️ Common Issues

### ❌ CORS errors

Fix backend CORS configuration

### ❌ Port already in use

Change port:

```yaml
server:
  port: 8081
```

### ❌ DB connection issues

Check credentials and DB availability

---

## 💡 Future Improvements

* URL expiration
* Analytics (click tracking)
* Reslience 4J
* Authentication (JWT)
* API Documentation
* Redis cache integration
* DB Migration
* DB indexing

---

## Run frontend application

1. Clone the repository
   ```bash
   git clone git@github.com:upendrakumbham/url-shortener-ui.git
   cd url-shortener-ui
   ```
2. Install dependencies
   npm install
3. Start the development server
   npm run dev
4. Open the application
   http://localhost:5173
   🔗 Backend Configuration

The frontend expects the backend API to be running at:
http://localhost:8080

If your backend runs on a different port, update:
src/api/urlApi.js
const API_BASE = "http://localhost:8080";

### 🌐 CORS Configuration (IMPORTANT)
```
Update your Spring Boot backend:

@CrossOrigin(origins = "http://localhost:5173")
Public class UrlController
Or configure globally via WebMvcConfigurer.
Rerun backend API, make sure it is running.
```
###  🧪 Available Scripts
```
npm run dev       # Start dev server
npm run build     # Build production bundle
npm run preview   # Preview production build
```

## 🧑‍💻 Author

Upendra Kumbham

---

## 📄 License

This project is implemented as part TpxImpact assignment.








