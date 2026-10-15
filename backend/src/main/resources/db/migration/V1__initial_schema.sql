CREATE TABLE groups (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(100) NOT NULL,
    base_currency CHAR(3)      NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE members (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id   UUID        NOT NULL REFERENCES groups (id) ON DELETE CASCADE,
    name       VARCHAR(60) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_members_group_id ON members (group_id);

CREATE UNIQUE INDEX uq_members_group_name ON members (group_id, lower(name));

CREATE TABLE expenses (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id          UUID           NOT NULL REFERENCES groups (id) ON DELETE CASCADE,
    payer_id          UUID           NOT NULL REFERENCES members (id) DEFERRABLE INITIALLY DEFERRED,
    description       VARCHAR(140)   NOT NULL,
    original_amount   NUMERIC(19, 2) NOT NULL CHECK (original_amount > 0),
    original_currency CHAR(3)        NOT NULL,
    exchange_rate     NUMERIC(19, 8) NOT NULL CHECK (exchange_rate > 0),
    amount            NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    split_type        VARCHAR(20)    NOT NULL CHECK (split_type IN ('EQUAL', 'PERCENTAGE', 'EXACT')),
    expense_date      DATE           NOT NULL DEFAULT CURRENT_DATE,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_expenses_group_id ON expenses (group_id);

CREATE TABLE expense_splits (
    id         UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID           NOT NULL REFERENCES expenses (id) ON DELETE CASCADE,
    member_id  UUID           NOT NULL REFERENCES members (id) DEFERRABLE INITIALLY DEFERRED,
    amount     NUMERIC(19, 2) NOT NULL CHECK (amount >= 0),
    percentage NUMERIC(5, 2)  CHECK (percentage > 0 AND percentage <= 100),
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT uq_expense_splits_expense_member UNIQUE (expense_id, member_id)
);

CREATE INDEX idx_expense_splits_member_id ON expense_splits (member_id);

CREATE TABLE payments (
    id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id       UUID           NOT NULL REFERENCES groups (id) ON DELETE CASCADE,
    from_member_id UUID           NOT NULL REFERENCES members (id) DEFERRABLE INITIALLY DEFERRED,
    to_member_id   UUID           NOT NULL REFERENCES members (id) DEFERRABLE INITIALLY DEFERRED,
    amount         NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_payments_distinct_members CHECK (from_member_id <> to_member_id)
);

CREATE INDEX idx_payments_group_id ON payments (group_id);
