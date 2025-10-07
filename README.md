# 💰 Finance Tracker Backend Overview

## ⚙️ Tech Stack
- Java 17  
- Spring Boot 3.x  
- MongoDB  
- Spring Security (JWT)  
- Lombok  
- SpringDoc OpenAPI (Swagger UI)  
- Maven  
- Logging: Slf4j  
- Exports: CSV, Excel, PDF (Apache POI & iTextPDF)  
- Email Alerts via JavaMailSender  

---

## 🧱 Main Modules

### 1️⃣ User Management
**Entity:** `UserEntity`  
Handles user registration, login, and authentication with JWT.

**Endpoints:**
- `POST /api/auth/register`
- `POST /api/auth/login`

---

### 2️⃣ Category Management
**Entity:** `CategoryEntity`  
Manages user and default categories for income/expenses.

**Endpoints:**
- `GET /api/categories`
- `POST /api/categories`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`

---

### 3️⃣ Transaction Management
**Entity:** `TransactionEntity`  
Stores all transactions with details and user/category linkage.

**Endpoints:**
- `GET /api/transactions`
- `POST /api/transactions`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`

---

### 4️⃣ Budget Tracking
**Entity:** `BudgetEntity`  
Tracks budget usage and triggers notifications when 80% or 100% of limit is reached.

**Endpoints:**
- `GET /api/budgets`
- `POST /api/budgets`
- `PUT /api/budgets/{id}`
- `DELETE /api/budgets/{id}`

---

### 5️⃣ Reports / Exports
**Service:** `ReportExportServiceImpl`  
Supports exporting transactions to CSV, Excel, and PDF.

**Endpoints:**
- `/api/reports/export/csv?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- `/api/reports/export/excel?...`
- `/api/reports/export/pdf?...`

**CSV Example:**
```
Date,Category,Type,Description,Amount
2025-09-22,Salary,INCOME,September Month Pay,8000.00
2025-09-22,Entertainment,EXPENSE,War 2,500.00
```

---

### 6️⃣ Email Notifications
**Service:** `EmailServiceImpl`  
Sends email alerts when a budget exceeds 80% or 100%.

**Example Call:**
```java
emailService.sendBudgetAlert(user, budget, percentageUsed);
```

---

### 7️⃣ Security
JWT-based authentication with role-based access.  
Secured endpoints require `Authorization: Bearer <token>` header.

---

### 8️⃣ Logging
Extensive logging with `@Slf4j`:
```java
log.info("Generating CSV export for user {}", userId);
log.warn("Budget '{}' exceeded the limit", budget.getName());
```

---

## 🔗 API Flow Summary

| Feature | Endpoint | Method | Auth | Request | Response |
|----------|-----------|---------|-------|----------|-----------|
| Register | `/api/auth/register` | POST | ❌ | `{username,email,password}` | User created |
| Login | `/api/auth/login` | POST | ❌ | `{email,password}` | `{token,user}` |
| Get Transactions | `/api/transactions` | GET | ✅ | — | List |
| Add Transaction | `/api/transactions` | POST | ✅ | `{categoryId,description,amount,date}` | Transaction saved |
| Export CSV | `/api/reports/export/csv?...` | GET | ✅ | — | File |
| Export Excel | `/api/reports/export/excel?...` | GET | ✅ | — | File |
| Export PDF | `/api/reports/export/pdf?...` | GET | ✅ | — | File |
| Add Budget | `/api/budgets` | POST | ✅ | `{categoryId,budgetAmount,startDate,endDate}` | Budget created |
| Get Categories | `/api/categories` | GET | ✅ | — | List |

---

## 🎯 Frontend (Angular) Objectives
When integrating with Angular frontend:

- Login / Register pages  
- Dashboard with summary cards (Income, Expense, Balance)  
- Manage Transactions, Categories, Budgets  
- Export Reports (CSV, Excel, PDF)  
- JWT interceptor for secured requests  
- Email alerts for budget overspend  

---

### ✅ Next Step
Use this document as the backend reference for your **Angular UI** integration.
