CREATE TABLE accounts (
    id              SERIAL PRIMARY KEY,
    bank_name       VARCHAR(100) NOT NULL,
    account_type    VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS', 'CREDIT_CARD', 'INVESTMENT')),
    holder_name     VARCHAR(100),
    account_number  VARCHAR(50),
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_types (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL,
    direction   VARCHAR(10) NOT NULL CHECK (direction IN ('INCOME', 'EXPENSE')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    parent_id   INT REFERENCES categories(id) ON DELETE SET NULL,
    icon        VARCHAR(50),
    color       VARCHAR(7),
    is_active   BOOLEAN DEFAULT true,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id                  SERIAL PRIMARY KEY,
    account_id          INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    transaction_type_id INT NOT NULL REFERENCES transaction_types(id),
    category_id         INT REFERENCES categories(id) ON DELETE SET NULL,
    transaction_date    DATE NOT NULL,
    description         TEXT NOT NULL,
    establishment       VARCHAR(200),
    amount              NUMERIC(12,2) NOT NULL,
    balance             NUMERIC(12,2),
    installment_info    VARCHAR(50),
    notes               TEXT,
    tags                TEXT[],
    imported_from       VARCHAR(50),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_amount ON transactions(amount);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

INSERT INTO transaction_types (name, direction) VALUES
('Income', 'INCOME'),
('Expense', 'EXPENSE');