CREATE TABLE users(
    id uuid primary key ,
    phone varchar(20) unique,
    email varchar(50) unique,
    created_at timestamp not null default now(),
    check (phone is not null or email is not null)
);


CREATE TABLE applications(
    id uuid primary key,
    user_id uuid not null references users(id),
    full_name varchar(100) not null ,
    age integer check(age > 0 and age <= 100) not null,
    gender char(1) not null check (gender in ('М', 'Ж')),
    marital_status varchar(15) not null check (marital_status in ('вдовец/вдова', 'в браке', 'одинок/а', 'разведен/а')),
    dependents integer check(dependents >= 0),
    education varchar(20) not null check (education in ('высшее', 'среднее', 'среднее специальное', 'ученая степень')),
    region varchar(50) not null check (region in ('Москва', 'Санкт-Петербург', 'региональный центр', 'другой город')),
    employment_type varchar(20) not null check (employment_type in ('безработный', 'бизнес', 'пенсионер', 'наемный работник', 'самозанятый')),
    employment_length integer check(employment_length >= 0),
    monthly_income integer not null check (monthly_income >= 0),
    additional_income integer check (additional_income >= 0),
    has_property boolean not null default false,
    has_car boolean not null default false,
    existing_cards integer check(existing_cards >= 0),
    existing_loans integer check (existing_loans >= 0),
    total_monthly_debt integer check (total_monthly_debt >= 0),
    has_salary_project boolean not null default false,
    has_deposit boolean not null default false,
    card_type_requested varchar(20) not null check (card_type_requested in ('золотая', 'платиновая', 'стандартная')),
    status varchar(20) not null check(status in ('IN_PROGRESS', 'COMPLETED', 'FAILED')),
    created_at timestamp not null default now()
);

CREATE TABLE application_decisions(
    id uuid primary key,
    application_id uuid unique not null references applications(id),
    bureau_score integer check (bureau_score >= 0),
    internal_score integer not null check (internal_score >= 0),
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


CREATE INDEX idx_applications_user_id_created_at ON applications(user_id, created_at);
CREATE INDEX idx_applications_status_created_at ON applications(status, created_at);