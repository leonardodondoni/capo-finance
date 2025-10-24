# 💰 Personal Finance Database Schema

## 📋 Overview

Complete database schema designed for importing and managing personal financial data from bank statements (extrato.csv) and credit card bills (fatura.csv).

## 🎯 Key Features

### ✅ Duplicate Prevention
- **File-level**: `imports` table uses SHA-256 hash to prevent re-importing the same file
- **Transaction-level**: Unique constraint on `(transaction_date, description, amount, source_type, account_id/credit_card_id)`
- **Incremental imports**: Same CSV can be imported multiple times safely - new transactions added, existing ones skipped

### ✅ Dual Source Support
- **EXTRATO** (Bank Statement): Linked to `accounts`, includes balance tracking
- **FATURA** (Credit Card): Linked to `credit_cards`, includes installment info and cardholder

### ✅ Shared Account Management
- Both Leonardo and Giovana use the same account/card
- Each transaction tracks `person_id` (responsibility)
- Supports individual and shared reporting

### ✅ Income Distribution Model ("Money Buckets")
- `income_distribution_rules` table defines target percentages per category
- Default: 10% Stability, 15% Financial Freedom, 55% Basic Needs, 10% Leisure, 10% Education
- `v_income_distribution_analysis` view compares actual vs target spending

## 📊 Database Tables

### Core Tables

#### 1️⃣ **people** - Who made the transaction
```sql
- id, name, email
- Defaults: Leonardo, Giovana
```

#### 2️⃣ **categories** - Master categories
```sql
- Expense: Leisure, Basic Needs, Financial Freedom, Stability, Education
- Income: Salary, PIX
- Fields: name, type (EXPENSE/INCOME), description, color, icon
```

#### 3️⃣ **subcategories** - Detailed classification
```sql
- Linked to category_id
- Examples:
  - Leisure: Restaurants, Entertainment, Travel, Subscriptions
  - Basic Needs: Groceries, Housing, Utilities, Transportation, Health, Pet Care
  - Education: Courses, Books, Certifications
```

#### 4️⃣ **accounts** - Bank accounts
```sql
- name, bank_name, account_type (CHECKING/SAVINGS/INVESTMENT)
- person_id, is_shared
- current_balance
```

#### 5️⃣ **credit_cards** - Credit cards with billing cycles
```sql
- name, last_four_digits, card_brand
- person_id, is_shared
- billing_day, due_day (important for bill management)
- credit_limit
```

#### 6️⃣ **imports** - Import history tracker
```sql
- import_type (EXTRATO/FATURA)
- file_name, file_hash (SHA-256 for duplicate detection)
- total_rows, imported_rows, skipped_rows, error_rows
- Links to account_id or credit_card_id
```

#### 7️⃣ **transactions** - All financial movements
```sql
Key fields:
- source_type (EXTRATO/FATURA)
- transaction_date, description, amount
- account_id OR credit_card_id
- category_id, subcategory_id
- person_id (responsibility)
- transaction_type (INCOME/EXPENSE/TRANSFER)

CSV-specific fields:
- balance_after (from extrato "Saldo")
- installment_info (from fatura "Parcela": "6 de 10")
- card_holder (from fatura "Portador")

Metadata:
- notes, tags[], is_recurring, is_verified
```

**Unique Constraint**: Prevents duplicate imports
```sql
UNIQUE (transaction_date, description, amount, source_type, account_id, credit_card_id)
```

### Planning & Analysis Tables

#### 8️⃣ **income_distribution_rules** - Money bucket percentages
```sql
- category_id, percentage
- person_id (optional: for individual rules)
- Pre-loaded with your desired distribution
```

#### 9️⃣ **budgets** - Monthly budget planning
```sql
- category_id, subcategory_id, person_id
- month_year (YYYY-MM)
- planned_amount, actual_amount
```

#### 🔟 **goals** - Financial goals tracking
```sql
- name, description
- target_amount, current_amount
- target_date, status (ACTIVE/COMPLETED/CANCELLED)
- category_id, person_id
```

## 🔍 Useful Views

### 1. `v_monthly_expenses_by_category`
Monthly spending breakdown by category and person

### 2. `v_income_distribution_analysis`
Compare actual spending vs target percentages (money bucket model)
- Shows target % vs actual % per category
- Helps track adherence to financial plan

### 3. `v_recent_transactions`
Last 100 transactions with full details (category, person, account, etc.)

## 📥 CSV Import Mapping

### Extrato.csv → transactions
```
Data           → transaction_date
Descricao      → description
Valor          → amount (detect income/expense by sign)
Saldo          → balance_after
               → source_type = 'EXTRATO'
               → account_id = (your checking account)
```

**Person detection logic** (description-based):
- "Pix enviado para Giovana" → Giovana
- "UBER" on Giovana's phone → Giovana
- Default → Leonardo

### Fatura.csv → transactions
```
Data           → transaction_date
Estabelecimento → description
Valor          → amount (always expense)
Parcela        → installment_info
Portador       → card_holder → person lookup
               → source_type = 'FATURA'
               → credit_card_id = (your credit card)
               → transaction_type = 'EXPENSE'
```

**Person mapping**:
- "LEONARDO SIQUEIRA" → Leonardo
- "GIOVANA DORNELES" → Giovana

## 🚀 Import Flow (Recommended)

### Step 1: Calculate file hash
```sql
-- Prevent duplicate file imports
SELECT id FROM imports WHERE file_hash = '<calculated-sha256>';
```

### Step 2: Create import record
```sql
INSERT INTO imports (import_type, file_name, file_hash, account_id, total_rows)
VALUES ('EXTRATO', 'extrato.csv', '<hash>', 1, 111)
RETURNING id;
```

### Step 3: Insert transactions with ON CONFLICT
```sql
INSERT INTO transactions (
    source_type, import_id, transaction_date, description, 
    amount, account_id, person_id, transaction_type, balance_after
)
VALUES (...)
ON CONFLICT (transaction_date, description, amount, source_type, account_id, credit_card_id)
DO NOTHING; -- Skip duplicates
```

### Step 4: Update import stats
```sql
UPDATE imports 
SET imported_rows = <count>, skipped_rows = <count>, status = 'SUCCESS'
WHERE id = <import_id>;
```

## 📈 Reporting Queries

### Monthly Income vs Expenses
```sql
SELECT 
    TO_CHAR(transaction_date, 'YYYY-MM') as month,
    transaction_type,
    SUM(amount) as total
FROM transactions
GROUP BY month, transaction_type
ORDER BY month DESC;
```

### Spending by Person
```sql
SELECT 
    p.name,
    c.name as category,
    COUNT(*) as transactions,
    SUM(t.amount) as total_spent
FROM transactions t
JOIN people p ON t.person_id = p.id
JOIN categories c ON t.category_id = c.id
WHERE t.transaction_type = 'EXPENSE'
  AND t.transaction_date >= DATE_TRUNC('month', CURRENT_DATE)
GROUP BY p.name, c.name
ORDER BY total_spent DESC;
```

### Money Bucket Adherence (Current Month)
```sql
SELECT * FROM v_income_distribution_analysis
WHERE month_year = TO_CHAR(CURRENT_DATE, 'YYYY-MM');
```

## 🔐 Data Integrity

### Constraints
- ✅ `CHECK` constraints for valid enum values
- ✅ `UNIQUE` constraints prevent duplicates
- ✅ `FOREIGN KEY` constraints maintain relationships
- ✅ `NOT NULL` on critical fields
- ✅ Conditional constraint: transactions must have EITHER account_id OR credit_card_id

### Triggers
- ✅ Auto-update `updated_at` timestamp on all main tables

### Indexes
- ✅ Performance indexes on frequently queried columns:
  - `transaction_date`, `person_id`, `category_id`, `source_type`

## 🎨 Category Auto-Classification (Future)

Implement smart categorization based on description keywords:
```sql
-- Example rules
'UBER' → Basic Needs / Transportation
'PETLOVE' → Basic Needs / Pet Care
'IFOOD' → Leisure / Restaurants & Bars
'AMAZON' → (varies - requires sub-analysis)
'Pix enviado' → (varies - check recipient)
```

## 🔄 Migration Path

1. ✅ V3 migration drops old tables and creates new schema
2. ✅ Seeds default data (people, categories, subcategories, distribution rules)
3. ✅ Ready for CSV imports

## 📝 Next Steps

1. **Build CSV parser** (backend service)
   - Read extrato.csv and fatura.csv
   - Calculate file hash
   - Map columns to transaction fields
   - Detect person from description/portador
   - Auto-categorize (or leave NULL for manual classification)

2. **Create import API endpoints**
   - `POST /api/imports/extrato` (upload CSV)
   - `POST /api/imports/fatura` (upload CSV)
   - `GET /api/imports` (history)
   - `GET /api/imports/{id}/transactions` (view imported data)

3. **Build classification UI**
   - Manual category assignment for uncategorized transactions
   - Bulk classification tools
   - Learn from past classifications

4. **Create dashboards**
   - Monthly income/expense chart
   - Money bucket progress bars
   - Category breakdown pie charts
   - Person comparison (Leonardo vs Giovana)

---

**Schema designed for scalability, data integrity, and ease of reporting.** 🚀
