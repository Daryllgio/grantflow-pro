create table users (
    id bigserial primary key,
    full_name varchar(255),
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    role varchar(50) not null,
    created_at timestamp
);

create table programs (
    id bigserial primary key,
    name varchar(255),
    description text,
    award_amount numeric(12,2),
    application_deadline date,
    eligibility_criteria text,
    status varchar(50),
    created_at timestamp,
    updated_at timestamp
);

create table applications (
    id bigserial primary key,
    applicant_id bigint not null references users(id),
    program_id bigint not null references programs(id),
    personal_statement text,
    academic_background text,
    financial_need_statement text,
    leadership_experience text,
    community_impact text,
    status varchar(50),
    risk_flag boolean default false,
    submitted_at timestamp,
    created_at timestamp,
    updated_at timestamp
);

create table application_documents (
    id bigserial primary key,
    application_id bigint not null references applications(id),
    document_type varchar(50),
    file_name varchar(255),
    storage_key varchar(500),
    document_url varchar(1000),
    uploaded_at timestamp
);

create table review_assignments (
    id bigserial primary key,
    application_id bigint not null references applications(id),
    reviewer_id bigint not null references users(id),
    completed boolean default false,
    assigned_at timestamp
);

create table reviews (
    id bigserial primary key,
    application_id bigint not null references applications(id),
    reviewer_id bigint not null references users(id),
    academic_score integer,
    leadership_score integer,
    financial_need_score integer,
    community_impact_score integer,
    essay_score integer,
    total_score integer,
    recommendation varchar(50),
    feedback text,
    reviewed_at timestamp
);

create table notifications (
    id bigserial primary key,
    recipient_id bigint not null references users(id),
    title varchar(255),
    message text,
    read boolean default false,
    created_at timestamp
);

create table audit_logs (
    id bigserial primary key,
    actor_id bigint references users(id),
    action varchar(255),
    entity_type varchar(255),
    entity_id bigint,
    details text,
    created_at timestamp
);
