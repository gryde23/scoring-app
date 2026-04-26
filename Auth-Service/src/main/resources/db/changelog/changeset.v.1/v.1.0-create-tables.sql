create table known_clients(
    id uuid primary key,
    phone varchar(15) unique not null,
    full_name varchar not null,
    is_active boolean not null,
    created_at timestamp
);

create table users(
    id uuid primary key,
    phone varchar(15) unique not null,
    password varchar not null,
    created_at timestamp
);

create table registration_verifications(
    id uuid primary key,
    phone varchar(15) not null,
    client_id uuid not null,
    code_hash varchar not null,
    status varchar not null,
    expires_at timestamp not null,
    attempts integer not null,
    registration_token_hash varchar,
    registration_token_expires_at timestamp,
    created_at timestamp
)