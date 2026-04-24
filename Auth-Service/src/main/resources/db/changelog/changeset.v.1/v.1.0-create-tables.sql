create table known_clients(
    id uuid primary key,
    phone varchar unique not null,
    full_name varchar not null,
    is_active boolean not null,
    created_at timestamp
);

create table users(
    id uuid primary key,
    phone varchar unique not null,
    password_hash varchar not null,
    created_at timestamp
);

create table verification_codes(
    id uuid primary key,
    phone varchar not null,
    code varchar not null,
    expires_at timestamp not null,
    used boolean not null,
    attempts integer not null,
    created_at timestamp
)