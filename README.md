# Eprofiling System

A full-stack eProfiling System organized into separate frontend, backend, and documentation directories.

## 📁 Project Structure

```text
eprofiling/
├── docs/
├── server/
└── web/
```

### `docs/`

Contains documentation and API testing resources.

```text
docs/
└── hoppscotch/
    └── *.json
```

The Hoppscotch JSON files contain API collections that can be imported into Hoppscotch for testing the backend API.

---

### `server/`

Contains the backend application.

The backend provides the REST API used by the web application.

#### Environment Variables

The server requires database connection environment variables:

```env
JDBC_DATABASE_URL=
JDBC_DATABASE_USERNAME=
JDBC_DATABASE_PASSWORD=
```

These variables are used by the application configuration:

```yaml
datasource:
  url: ${JDBC_DATABASE_URL}
  username: ${JDBC_DATABASE_USERNAME}
  password: ${JDBC_DATABASE_PASSWORD}
```

Set these environment variables before running the server.

To run the server locally:

```bash
cd server
./gradlew bootRun
```

On Windows:

```bash
cd server
gradlew.bat bootRun
```

---

### `web/`

Contains the frontend application.

The frontend communicates with the backend through its REST API.

To run the web application locally:

```bash
cd web
npm install
npm run dev
```

The development server will normally be available at:

```text
http://localhost:5173
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ralphmarondev/Registry.git
cd Registry
```

### 2. Configure the Backend

Navigate to the server directory:

```bash
cd server
```

Set the required environment variables:

```env
JDBC_DATABASE_URL=
JDBC_DATABASE_USERNAME=
JDBC_DATABASE_PASSWORD=
```

### 3. Start the Backend

```bash
./gradlew bootRun
```

On Windows:

```bash
gradlew.bat bootRun
```

### 4. Start the Frontend

Open another terminal and navigate to the web directory:

```bash
cd web
npm install
npm run dev
```

### 5. Test the API

Import the Hoppscotch JSON collection from the `docs/` directory into Hoppscotch and configure the appropriate backend URL.

## 📌 Development

Keep frontend, backend, and documentation changes organized within their respective directories:

* **Frontend** → `web/`
* **Backend / API** → `server/`
* **Documentation / API Testing** → `docs/`

