-- V6: metas semanales
create table weekly_goal (
    id          bigserial primary key,
    metric      varchar(32) not null unique,
    target      integer not null check (target > 0),
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);
