CREATE TABLE applications(
    id uuid primary key,
    user_id uuid not null,
    full_name varchar(100) not null,
    age integer check(age > 0 and age <= 100) not null,
    marital_status varchar(10) not null check (marital_status in ('WIDOWED', 'MARRIED', 'SINGLE', 'DIVORCED')),
    dependents integer check(dependents >= 0),
    education varchar(20) not null check (education in ('HIGHER', 'SECONDARY', 'SECONDARY_SPECIAL', 'PHD')),
    region varchar(50) not null check (region in ('MOSCOW', 'SAINT_PETERSBURG', 'REGIONAL_CENTER', 'OTHER_CITY')),
    employment_type varchar(20) not null check (employment_type in ('UNEMPLOYED', 'BUSINESS', 'PENSIONER', 'EMPLOYEE', 'SELF_EMPLOYED')),
    employment_length integer check(employment_length >= 0),
    monthly_income integer not null check (monthly_income >= 0),
    additional_income integer check (additional_income >= 0),
    has_property boolean not null default false,
    has_car boolean not null default false,
    has_salary_project boolean not null default false,
    has_deposit boolean not null default false,
    card_type_requested varchar(20) not null check (card_type_requested in ('GOLD', 'PLATINUM', 'STANDARD')),
    status varchar(20) not null check(status in ('IN_PROGRESS', 'COMPLETED', 'FAILED')),
    created_at timestamp not null default now()
);

CREATE TABLE application_decisions(
    application_id uuid primary key references applications(id),
    bureau_score integer check (bureau_score >= 0),
    internal_score integer check (internal_score >= 0),
    ml_default_probability numeric(5,4) check(ml_default_probability >= 0 and ml_default_probability <= 1),
    antifraud_score integer check (antifraud_score >= 0),
    antifraud_flags jsonb,
    final_decision varchar(15) not null check (final_decision in ('APPROVED', 'REJECTED', 'MANUAL_REVIEW')),
    approved_limit integer,
    decision_reasons jsonb,
    created_at timestamp not null default now(),
    CONSTRAINT chk_application_decisions_approved_limit CHECK (
        (final_decision = 'APPROVED' and approved_limit is not null and approved_limit >= 0)
            or
        (final_decision in ('REJECTED', 'MANUAL_REVIEW') and approved_limit is null)
        )
);

create table application_bureau_snapshot (
    application_id uuid primary key references applications(id),

    total_accounts integer not null check (total_accounts >= 0),
    active_accounts integer not null check (active_accounts >= 0),
    closed_accounts integer not null check (closed_accounts >= 0),
    default_accounts integer not null check (default_accounts >= 0),
    restructured_accounts integer not null check (restructured_accounts >= 0),

    credit_history_days integer not null check (credit_history_days >= 0),
    total_credit_limit numeric(14,2) not null check (total_credit_limit >= 0),
    total_active_debt numeric(14,2) not null check (total_active_debt >= 0),
    utilization_ratio numeric(8,4) not null check (utilization_ratio >= 0),

    total_payments integer not null check (total_payments >= 0),
    dpd30 integer not null check (dpd30 >= 0),
    dpd60 integer not null check (dpd60 >= 0),
    dpd90 integer not null check (dpd90 >= 0),
    dpd90_plus integer not null check (dpd90_plus >= 0),
    max_days_overdue integer not null check (max_days_overdue >= 0),

    payment_ratio numeric(8,4) not null check (payment_ratio >= 0),
    partial_payments_count integer not null check (partial_payments_count >= 0),
    recent_overdue_count integer not null check (recent_overdue_count >= 0),

    monthly_debt_payment numeric(14,2) not null check (monthly_debt_payment >= 0),
    debt_to_income numeric(8,4) not null check (debt_to_income >= 0),

    bureau_score integer,
    created_at timestamp not null default now()
);

CREATE INDEX idx_applications_user_id_created_at ON applications(user_id, created_at);
CREATE INDEX idx_applications_status_created_at ON applications(status, created_at);