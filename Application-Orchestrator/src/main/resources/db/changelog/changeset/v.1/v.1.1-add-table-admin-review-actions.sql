create table admin_review_actions(
    id uuid primary key,
    application_id uuid not null references applications(id),
    employee_id uuid not null,
    action_type varchar(30) not null,
    comment varchar,
    old_value text,
    new_value text,
    created_at timestamp not null default now()
);