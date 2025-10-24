# 🧭 Capo Finance — Copilot Instructions

This document defines **project conventions**, **architectural principles**, and **coding standards** to guide GitHub Copilot’s suggestions and maintain a consistent, clean, and scalable codebase across **frontend** and **backend**.

---

## 🏗️ General Principles

Capo Finance follows the **SOLID**, **DRY**, and **Clean Code** principles throughout the entire stack.

* **Readability > Cleverness** – Prefer explicit, simple, and self-documenting code.
* **Consistency** – Use consistent naming, structure, and formatting conventions.
* **Scalability** – Design code that’s easy to extend without major refactoring.
* **Separation of Concerns** – Keep logic, UI, and data access isolated.
* **Testability** – Write components and classes that can be easily tested.
* **Language** – Use English for all code, comments, and documentation.
* **Comments** – Do not make  unnecessary comments; code should be self-explanatory. Use comments only for complex logic explanations.

---

## 🎨 Frontend (React + TypeScript + Vite + MobX)

### 📁 Folder Structure

```
frontend/
├── src/
│   ├── api/              # API request definitions (Axios-based)
│   ├── scenes/           # High-level pages or views (each with its own logic + UI)
│   │   ├── Dashboard/
│   │   │   ├── index.ts  # Main scene logic, data fetching, MobX state setup
│   │   │   ├── Dashboard.tsx  # Presentation/UI component
│   │   │   └── components/    # Scene-specific subcomponents
│   ├── components/       # Global reusable UI components
│   ├── store/            # MobX stores (global state management)
│   ├── types/            # Shared TypeScript types and interfaces
│   ├── utils/            # Utility functions (formatting, math, date, etc.)
│   └── main.tsx          # Application entry point
```

---

### ⚙️ Code Conventions

#### Components

* Use **PascalCase** for component and scene names (`TransactionList.tsx`, `Dashboard.tsx`).
* Use **camelCase** for internal variables and functions.
* Every scene has:

  * `index.ts`: scene logic, MobX local store, and data fetching.
  * `SceneName.tsx`: UI/presentation only (pure components).

#### Example Scene Structure

```tsx
// scenes/Transactions/index.ts
import { makeAutoObservable } from "mobx";

class TransactionsSceneStore {
  transactions = [];

  constructor() {
    makeAutoObservable(this);
  }

  async fetchTransactions() {
    // call API and update state
  }
}

export const transactionsSceneStore = new TransactionsSceneStore();
```

```tsx
// scenes/Transactions/Transactions.tsx
import { observer } from "mobx-react-lite";
import { transactionsSceneStore } from "./index";

export const Transactions = observer(() => (
  <div>
    {transactionsSceneStore.transactions.map((t) => (
      <div key={t.id}>{t.description}</div>
    ))}
  </div>
));
```

---

### 🧠 Global State (MobX)

* Use a single global store context via `store/RootStore.ts`.
* Each store must be **class-based** and registered in the root store.
* Access via a custom hook `useStore()` for cleaner syntax.

---

### 🌐 API Layer

* All API calls live in `/src/api`, grouped by resource (e.g., `transactionsApi.ts`).
* Use Axios with a pre-configured base instance.
* Each file should export clear, typed functions (`getTransactions`, `createTransaction`, etc.).

**Example:**

```ts
// api/transactionsApi.ts
import { api } from "./apiClient";
import { Transaction } from "../types/Transaction";

export const transactionsApi = {
  getAll: async (): Promise<Transaction[]> => {
    const { data } = await api.get("/transactions");
    return data;
  },
};
```

---

### 🎨 Styling

* Use **Tailwind CSS** for styling.
* Avoid inline styles except for dynamic one-offs.
* Use design tokens (spacing, colors) consistently.

---

### 📚 Imports

* Use absolute imports (`@/components`, `@/store`) via path aliases in `tsconfig.json`.

---

## ⚙️ Backend (Spring Boot + Java 17 + PostgreSQL + Flyway)

### 📁 Folder Structure

```
backend/
├── src/main/java/com/capofinance/
│   ├── application/    # Use cases / service layer (business logic)
│   ├── domain/         # Entities, value objects, domain rules
│   ├── infrastructure/ # Repositories, persistence, config, adapters
│   └── presentation/   # Controllers and DTOs (API layer)
├── src/main/resources/
│   ├── db/migration/   # Flyway SQL migrations (V1__init_schema.sql, etc.)
│   └── application.yml # Configurations
```

---

### 🧩 Naming Conventions

| Type                | Convention          | Example                                             |
| ------------------- | ------------------- | --------------------------------------------------- |
| Packages            | lowercase, singular | `service`, `repository`                             |
| Classes             | PascalCase          | `TransactionService`, `AccountRepository`           |
| Variables & Methods | camelCase           | `transactionType`, `calculateBalance()`             |
| Database Tables     | snake_case          | `transactions`, `accounts`                          |
| SQL Files           | Versioned           | `V1__init_schema.sql`, `V2__add_balance_column.sql` |

---

### 🧱 Flyway Migrations

All schema definitions and changes must go under:

```
src/main/resources/db/migration/
```

**Example:**

```sql
-- V1__init_schema.sql
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    balance DECIMAL(14,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES accounts(id),
    transaction_type VARCHAR(50),
    amount DECIMAL(14,2) NOT NULL,
    description TEXT,
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 🧠 Backend Architecture Rules

* **Controllers**: Handle only HTTP requests/responses and delegate logic to services.
* **Services**: Contain business logic, validation, and rules.
* **Repositories**: Handle persistence (Spring Data JPA).
* **Entities**: Represent domain models, not tied directly to DTOs.
* **DTOs**: Define clear boundaries for API inputs/outputs.

---

### ✅ Code Style & Practices

* Follow **SOLID** principles:

  * Single Responsibility per class.
  * Dependency Injection for all services.
* Use **Lombok** for boilerplate reduction (`@Getter`, `@Builder`, `@RequiredArgsConstructor`).
* Apply **DRY** by centralizing logic in shared services/utilities.
* Use `@Transactional` on service-level operations that modify data.
* Prefer immutability for domain objects when possible.

---

### 🌐 API Design

* RESTful, resource-oriented endpoints.
* Use plural nouns (`/api/transactions`, `/api/accounts`).
* Return DTOs, not entities.
* Use `ResponseEntity` with proper HTTP codes (`200 OK`, `201 Created`, `400 Bad Request`).

---

### 🧪 Testing

* Unit tests for service logic (`@SpringBootTest`, JUnit 5).
* Integration tests for repositories and controllers.
* Mock external dependencies with **Mockito**.

---

## 🔄 Naming Consistency Across Stack

| Concept     | Backend (Java)         | Database (SQL)     | Frontend (TS)     |
| ----------- | ---------------------- | ------------------ | ----------------- |
| Transaction | `TransactionEntity`    | `transactions`     | `Transaction`     |
| Account     | `AccountEntity`        | `accounts`         | `Account`         |
| Balance     | `balance`              | `balance`          | `balance`         |
| Type        | `TransactionType` enum | `transaction_type` | `transactionType` |

---

## 📘 Summary of Key Rules

### ✅ Frontend

* Scenes contain logic in `index.ts` and UI in `SceneName.tsx`.
* MobX for global and local state.
* Axios for typed API requests.
* Tailwind for styling.
* Absolute imports for consistency.

### ✅ Backend

* Clean architecture: Controller → Service → Repository.
* Manage schema via **Flyway**.
* Follow **SOLID + DRY + Clean Code**.
* Consistent naming and DTO-based APIs.

---

*This file serves as a guide for both human contributors and GitHub Copilot to maintain consistency, clarity, and quality across the Capo Finance project.*