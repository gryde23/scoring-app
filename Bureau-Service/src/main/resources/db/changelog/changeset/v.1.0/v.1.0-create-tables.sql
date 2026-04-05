create table credit_accounts(
    id UUID primary key,
    user_id UUID,
    phone varchar(20) not null,
    account_type varchar(30) check (account_type in ('CREDIT_CARD', 'CONSUMER_LOAN', 'MORTGAGE', 'MICROLOAN')) not null,
    open_date date not null,
    close_date date check(close_date > open_date),
    original_amount decimal(12,2) check(original_amount > 0) not null,
    current_balance decimal(12,2) check(current_balance >= 0 and current_balance <= original_amount) not null,
    status varchar(20) check(status in ('ACTIVE', 'CLOSED', 'DEFAULT', 'RESTRUCTURED')) not null,
    bank_name varchar(100)
);

create table payment_history(
    id UUID primary key,
    account_id UUID references credit_accounts(id),
    due_date date not null,
    amount_due decimal(10,2) not null,
    amount_paid decimal(10,2) not null,
    days_overdue integer default 0,
    status varchar(20) check(status in ('PAID', 'OVERDUE', 'PARTIAL'))
);

CREATE INDEX idx_accounts_user ON credit_accounts(user_id);
CREATE INDEX idx_accounts_user_phone ON credit_accounts(phone);
CREATE INDEX idx_payments_account ON payment_history(account_id);