-- V3__create_personal_finance_schema.sql
-- Complete database schema for personal finance management
-- Supports incremental imports from bank statements (extrato) and credit card bills (fatura)

-- =====================================================
-- DROP EXISTING TABLES (clean slate)
-- =====================================================
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS subcategories CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS people CASCADE;
DROP TABLE IF EXISTS credit_cards CASCADE;
DROP TABLE IF EXISTS imports CASCADE;
DROP TABLE IF EXISTS income_distribution_rules CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS goals CASCADE;

-- =====================================================
-- PEOPLE - Who made or is responsible for transactions
-- =====================================================
CREATE TABLE people (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default people
INSERT INTO people (name, email) VALUES 
    ('Leonardo', 'leonardo@example.com'),
    ('Giovana', 'giovana@example.com');

-- =====================================================
-- CATEGORIES - Master categories for expenses and income
-- =====================================================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')),
    description TEXT,
    color VARCHAR(7), -- Hex color code for UI (#FF5733)
    icon VARCHAR(50), -- Icon name/code for UI
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert expense categories
INSERT INTO categories (name, type, description, sort_order) VALUES
    ('Leisure', 'EXPENSE', 'Entertainment, hobbies, fun activities', 1),
    ('Basic Needs', 'EXPENSE', 'Essential living expenses (food, housing, utilities)', 2),
    ('Financial Freedom', 'EXPENSE', 'Investments, savings, wealth building', 3),
    ('Stability', 'EXPENSE', 'Emergency fund, insurance, security', 4),
    ('Education', 'EXPENSE', 'Learning, courses, books, personal development', 5);

-- Insert income categories
INSERT INTO categories (name, type, description, sort_order) VALUES
    ('Salary', 'INCOME', 'Regular employment income', 1),
    ('PIX', 'INCOME', 'PIX transfers received', 2);

-- =====================================================
-- SUBCATEGORIES - Detailed classification within categories
-- =====================================================
CREATE TABLE subcategories (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(category_id, name)
);

-- Leisure subcategories
INSERT INTO subcategories (category_id, name, description, sort_order) 
SELECT id, 'Restaurants & Bars', 'Dining out, bars, cafes', 1 FROM categories WHERE name = 'Leisure'
UNION ALL
SELECT id, 'Entertainment', 'Movies, concerts, events', 2 FROM categories WHERE name = 'Leisure'
UNION ALL
SELECT id, 'Hobbies', 'Sports, games, collections', 3 FROM categories WHERE name = 'Leisure'
UNION ALL
SELECT id, 'Travel', 'Vacations, trips, tourism', 4 FROM categories WHERE name = 'Leisure'
UNION ALL
SELECT id, 'Subscriptions', 'Streaming, apps, memberships', 5 FROM categories WHERE name = 'Leisure';

-- Basic Needs subcategories
INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'Groceries', 'Supermarket, food shopping', 1 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Housing', 'Rent, mortgage, condo fees', 2 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Utilities', 'Electricity, water, gas, internet', 3 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Transportation', 'Uber, gas, car maintenance', 4 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Health', 'Medicine, doctor visits, insurance', 5 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Personal Care', 'Haircut, cosmetics, hygiene', 6 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Clothing', 'Clothes, shoes, accessories', 7 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 'Pet Care', 'Pet food, vet, pet supplies', 8 FROM categories WHERE name = 'Basic Needs';

-- Financial Freedom subcategories
INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'Investments', 'Stocks, funds, crypto', 1 FROM categories WHERE name = 'Financial Freedom'
UNION ALL
SELECT id, 'Savings', 'Emergency fund, reserves', 2 FROM categories WHERE name = 'Financial Freedom'
UNION ALL
SELECT id, 'Debt Payment', 'Credit card, loans', 3 FROM categories WHERE name = 'Financial Freedom';

-- Stability subcategories
INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'Insurance', 'Health, life, car insurance', 1 FROM categories WHERE name = 'Stability'
UNION ALL
SELECT id, 'Emergency Fund', 'Safety net, reserves', 2 FROM categories WHERE name = 'Stability'
UNION ALL
SELECT id, 'Taxes', 'Income tax, property tax', 3 FROM categories WHERE name = 'Stability';

-- Education subcategories
INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'Courses', 'Online courses, workshops', 1 FROM categories WHERE name = 'Education'
UNION ALL
SELECT id, 'Books', 'Books, e-books, audiobooks', 2 FROM categories WHERE name = 'Education'
UNION ALL
SELECT id, 'Certifications', 'Professional certifications', 3 FROM categories WHERE name = 'Education'
UNION ALL
SELECT id, 'Tools & Software', 'Learning tools, software licenses', 4 FROM categories WHERE name = 'Education';

-- Income subcategories
INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'Monthly Salary', 'Regular monthly income', 1 FROM categories WHERE name = 'Salary'
UNION ALL
SELECT id, 'Bonus', 'Performance bonus, 13th salary', 2 FROM categories WHERE name = 'Salary'
UNION ALL
SELECT id, 'Freelance', 'Freelance work, side projects', 3 FROM categories WHERE name = 'Salary';

INSERT INTO subcategories (category_id, name, description, sort_order)
SELECT id, 'PIX Received', 'Money received via PIX', 1 FROM categories WHERE name = 'PIX'
UNION ALL
SELECT id, 'Reimbursement', 'Money returned, refunds', 2 FROM categories WHERE name = 'PIX';

-- =====================================================
-- ACCOUNTS - Bank accounts
-- =====================================================
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bank_name VARCHAR(100),
    account_type VARCHAR(50) CHECK (account_type IN ('CHECKING', 'SAVINGS', 'INVESTMENT')),
    person_id INTEGER REFERENCES people(id),
    is_shared BOOLEAN DEFAULT FALSE, -- If shared between people
    currency VARCHAR(3) DEFAULT 'BRL',
    current_balance DECIMAL(14,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(name, person_id)
);

-- Insert default shared account
INSERT INTO accounts (name, bank_name, account_type, is_shared, current_balance) VALUES
    ('Conta Corrente Compartilhada', 'Banco do Brasil', 'CHECKING', TRUE, 0.00);

-- =====================================================
-- CREDIT_CARDS - Credit cards with billing cycles
-- =====================================================
CREATE TABLE credit_cards (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_four_digits VARCHAR(4),
    card_brand VARCHAR(50), -- Visa, Mastercard, etc.
    person_id INTEGER REFERENCES people(id),
    is_shared BOOLEAN DEFAULT FALSE,
    credit_limit DECIMAL(14,2),
    billing_day INTEGER CHECK (billing_day BETWEEN 1 AND 31), -- Day of month when bill closes
    due_day INTEGER CHECK (due_day BETWEEN 1 AND 31), -- Payment due day
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(name, person_id)
);

-- Insert default shared credit card
INSERT INTO credit_cards (name, last_four_digits, card_brand, is_shared, billing_day, due_day) VALUES
    ('Cartão Compartilhado', '0000', 'Visa', TRUE, 8, 20);

-- =====================================================
-- IMPORTS - Track import history to prevent duplicates
-- =====================================================
CREATE TABLE imports (
    id SERIAL PRIMARY KEY,
    import_type VARCHAR(20) NOT NULL CHECK (import_type IN ('EXTRATO', 'FATURA')),
    file_name VARCHAR(255) NOT NULL,
    file_hash VARCHAR(64) NOT NULL, -- SHA-256 hash of file content
    import_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_rows INTEGER,
    imported_rows INTEGER,
    skipped_rows INTEGER,
    error_rows INTEGER,
    account_id INTEGER REFERENCES accounts(id),
    credit_card_id INTEGER REFERENCES credit_cards(id),
    notes TEXT,
    status VARCHAR(20) DEFAULT 'SUCCESS' CHECK (status IN ('SUCCESS', 'PARTIAL', 'FAILED')),
    UNIQUE(file_hash) -- Prevent importing the same file twice
);

-- =====================================================
-- TRANSACTIONS - All financial movements
-- =====================================================
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    
    -- Source information
    source_type VARCHAR(20) NOT NULL CHECK (source_type IN ('EXTRATO', 'FATURA')),
    import_id INTEGER REFERENCES imports(id),
    
    -- Transaction identification (for duplicate detection)
    transaction_date TIMESTAMP NOT NULL,
    description TEXT NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    
    -- Account/Card linkage
    account_id INTEGER REFERENCES accounts(id),
    credit_card_id INTEGER REFERENCES credit_cards(id),
    
    -- Classification
    category_id INTEGER REFERENCES categories(id),
    subcategory_id INTEGER REFERENCES subcategories(id),
    person_id INTEGER NOT NULL REFERENCES people(id), -- Who is responsible
    
    -- Additional fields from CSVs
    balance_after DECIMAL(14,2), -- Saldo (from extrato)
    installment_info VARCHAR(50), -- Parcela (from fatura: "6 de 10")
    card_holder VARCHAR(100), -- Portador (from fatura)
    
    -- Transaction type
    transaction_type VARCHAR(20) CHECK (transaction_type IN ('INCOME', 'EXPENSE', 'TRANSFER')),
    
    -- Metadata
    notes TEXT,
    tags TEXT[], -- For future tagging system
    is_recurring BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE, -- Manual verification flag
    
    -- Duplicate prevention: composite unique constraint
    -- Same date + description + amount + source = likely duplicate
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (
        (source_type = 'EXTRATO' AND account_id IS NOT NULL) OR
        (source_type = 'FATURA' AND credit_card_id IS NOT NULL)
    ),
    CONSTRAINT unique_transaction UNIQUE (transaction_date, description, amount, source_type, COALESCE(account_id, 0), COALESCE(credit_card_id, 0))
);

-- Indexes for performance
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);
CREATE INDEX idx_transactions_person ON transactions(person_id);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_transactions_subcategory ON transactions(subcategory_id);
CREATE INDEX idx_transactions_source ON transactions(source_type, import_id);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);

-- =====================================================
-- INCOME_DISTRIBUTION_RULES - Money bucket allocation
-- =====================================================
CREATE TABLE income_distribution_rules (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    percentage DECIMAL(5,2) NOT NULL CHECK (percentage >= 0 AND percentage <= 100),
    person_id INTEGER REFERENCES people(id), -- If specific to a person
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(category_id, person_id)
);

-- Insert default distribution model (for expense categories only)
INSERT INTO income_distribution_rules (category_id, percentage)
SELECT id, 10.00 FROM categories WHERE name = 'Stability'
UNION ALL
SELECT id, 15.00 FROM categories WHERE name = 'Financial Freedom'
UNION ALL
SELECT id, 55.00 FROM categories WHERE name = 'Basic Needs'
UNION ALL
SELECT id, 10.00 FROM categories WHERE name = 'Leisure'
UNION ALL
SELECT id, 10.00 FROM categories WHERE name = 'Education';

-- =====================================================
-- BUDGETS - Monthly budget planning
-- =====================================================
CREATE TABLE budgets (
    id SERIAL PRIMARY KEY,
    category_id INTEGER REFERENCES categories(id),
    subcategory_id INTEGER REFERENCES subcategories(id),
    person_id INTEGER REFERENCES people(id),
    month_year VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    planned_amount DECIMAL(14,2) NOT NULL,
    actual_amount DECIMAL(14,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(category_id, subcategory_id, person_id, month_year)
);

-- =====================================================
-- GOALS - Financial goals tracking
-- =====================================================
CREATE TABLE goals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_amount DECIMAL(14,2) NOT NULL,
    current_amount DECIMAL(14,2) DEFAULT 0.00,
    target_date DATE,
    category_id INTEGER REFERENCES categories(id),
    person_id INTEGER REFERENCES people(id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- =====================================================
-- TRIGGERS - Auto-update timestamps
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_people_updated_at BEFORE UPDATE ON people
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_subcategories_updated_at BEFORE UPDATE ON subcategories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_credit_cards_updated_at BEFORE UPDATE ON credit_cards
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_goals_updated_at BEFORE UPDATE ON goals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- VIEWS - Useful queries for reporting
-- =====================================================

-- Monthly expense summary by category
CREATE OR REPLACE VIEW v_monthly_expenses_by_category AS
SELECT 
    TO_CHAR(transaction_date, 'YYYY-MM') as month_year,
    c.name as category,
    p.name as person,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount
FROM transactions t
JOIN categories c ON t.category_id = c.id
JOIN people p ON t.person_id = p.id
WHERE t.transaction_type = 'EXPENSE'
GROUP BY month_year, c.name, p.name
ORDER BY month_year DESC, total_amount DESC;

-- Income distribution analysis
CREATE OR REPLACE VIEW v_income_distribution_analysis AS
SELECT 
    TO_CHAR(t.transaction_date, 'YYYY-MM') as month_year,
    c.name as category,
    idr.percentage as target_percentage,
    SUM(t.amount) as actual_amount,
    (SELECT SUM(amount) FROM transactions 
     WHERE transaction_type = 'INCOME' 
     AND TO_CHAR(transaction_date, 'YYYY-MM') = TO_CHAR(t.transaction_date, 'YYYY-MM')
    ) as total_income,
    ROUND(
        (SUM(t.amount) / NULLIF(
            (SELECT SUM(amount) FROM transactions 
             WHERE transaction_type = 'INCOME' 
             AND TO_CHAR(transaction_date, 'YYYY-MM') = TO_CHAR(t.transaction_date, 'YYYY-MM')
            ), 0
        ) * 100), 2
    ) as actual_percentage
FROM transactions t
JOIN categories c ON t.category_id = c.id
LEFT JOIN income_distribution_rules idr ON idr.category_id = c.id
WHERE t.transaction_type = 'EXPENSE'
GROUP BY month_year, c.name, idr.percentage
ORDER BY month_year DESC, actual_amount DESC;

-- Recent transactions with full details
CREATE OR REPLACE VIEW v_recent_transactions AS
SELECT 
    t.id,
    t.transaction_date,
    t.description,
    t.amount,
    t.transaction_type,
    t.source_type,
    c.name as category,
    sc.name as subcategory,
    p.name as person,
    COALESCE(a.name, cc.name) as account_or_card,
    t.installment_info,
    t.is_verified
FROM transactions t
LEFT JOIN categories c ON t.category_id = c.id
LEFT JOIN subcategories sc ON t.subcategory_id = sc.id
JOIN people p ON t.person_id = p.id
LEFT JOIN accounts a ON t.account_id = a.id
LEFT JOIN credit_cards cc ON t.credit_card_id = cc.id
ORDER BY t.transaction_date DESC
LIMIT 100;

-- =====================================================
-- COMMENTS - Documentation
-- =====================================================
COMMENT ON TABLE transactions IS 'All financial movements from bank statements and credit card bills';
COMMENT ON COLUMN transactions.source_type IS 'Origin: EXTRATO (bank statement) or FATURA (credit card bill)';
COMMENT ON COLUMN transactions.installment_info IS 'Installment details from credit card (e.g., "6 de 10")';
COMMENT ON COLUMN transactions.balance_after IS 'Account balance after transaction (from bank statement)';
COMMENT ON TABLE imports IS 'Import history to prevent duplicate imports using file hash';
COMMENT ON TABLE income_distribution_rules IS 'Money bucket model: allocate income percentages to categories';
