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
    status varchar not null,
    provider_check_id varchar not null,
    call_phone varchar(15) not null,
    expires_at timestamp not null,
    created_at timestamp
)