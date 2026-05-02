alter table users add column role varchar(10);

update users
set role = 'USER'
where role IS NULL;

alter table users alter column role set not null;