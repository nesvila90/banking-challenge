create table if not exists persona
(
    id            uuid                     default uuid_generate_v4() not null
    primary key,
    name          varchar(255)                                        not null,
    genre         varchar(50)                                         not null,
    date_of_birth date                                                not null,
    code_id       varchar(255)                                        not null
    unique,
    address       varchar(255)                                        not null,
    telephone     varchar(255)                                        not null,
    created_at    timestamp with time zone default now()              not null,
    updated_at    timestamp with time zone default now()              not null
    );

alter table persona
    owner to postgres;

create index if not exists idx_persona_code_id
    on persona (code_id);

create table if not exists cliente
(
    id         uuid                     default uuid_generate_v4() not null
    primary key,
    person_id  uuid                                                not null
    unique
    constraint fk_persona
    references persona
    on delete cascade,
    username   varchar(255)                                        not null
    unique,
    password   varchar(255)                                        not null,
    status     boolean                  default true               not null,
    created_at timestamp with time zone default now()              not null,
    updated_at timestamp with time zone default now()              not null
    );

alter table cliente
    owner to postgres;

create index if not exists idx_cliente_person_id
    on cliente (person_id);

create table if not exists account
(
    id              uuid                     default gen_random_uuid() not null
    primary key,
    owner_id        uuid                                               not null
    constraint fk_account_owner
    references cliente
    on update cascade on delete cascade,
    account_number  varchar(30)                                        not null
    unique,
    account_type    varchar(20)                                        not null
    constraint account_account_type_check
    check ((account_type)::text = ANY
((ARRAY ['SAVINGS'::character varying, 'CHECKING'::character varying])::text[])),
    initial_balance numeric(14, 2)                                     not null
    constraint account_initial_balance_check
    check (initial_balance >= (0)::numeric),
    current_balance numeric(14, 2)                                     not null
    constraint account_current_balance_check
    check (current_balance >= (0)::numeric),
    status          boolean                  default true              not null,
    version         bigint                   default 0                 not null,
    created_at      timestamp with time zone default now()             not null,
    updated_at      timestamp with time zone default now()             not null
    );

alter table account
    owner to postgres;

create index if not exists ix_account_owner
    on account (owner_id);

create index if not exists idx_account_owner_id
    on account (owner_id);

create table if not exists movement
(
    id            uuid                     default gen_random_uuid() not null
    primary key,
    account_id    uuid                                               not null
    references account
    on delete cascade,
    at            timestamp with time zone default now()             not null,
    movement_type varchar(20)                                        not null
    constraint movement_movement_type_check
    check ((movement_type)::text = ANY
((ARRAY ['DEPOSIT'::character varying, 'WITHDRAW'::character varying])::text[])),
    amount        numeric(14, 2)                                     not null
    constraint movement_amount_check
    check (amount > (0)::numeric),
    balance_after numeric(14, 2)                                     not null
    constraint movement_balance_after_check
    check (balance_after >= (0)::numeric),
    created_at    timestamp with time zone default now()             not null
    );

alter table movement
    owner to postgres;

create index if not exists ix_movement_account_at
    on movement (account_id asc, at desc);
