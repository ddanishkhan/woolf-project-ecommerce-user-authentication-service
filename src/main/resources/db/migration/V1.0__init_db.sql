create table roles (created_at datetime(6), id bigint not null auto_increment, description varchar(255), name enum ('ADMIN','USER') not null, primary key (id));

create table sessions (expiring_at datetime(6), id bigint not null auto_increment, login_at datetime(6), user_id bigint, token varchar(255), session_status enum ('ACTIVE','ENDED','INVALID'), primary key (id));

create table users (created_at datetime(6), id bigint not null auto_increment, role_id bigint not null, updated_at datetime(6), email varchar(255) not null, password varchar(255) not null, primary key (id));

alter table roles add constraint UK_ROLES_NAME unique (name);
alter table users add constraint UK_USERS_ROLE_ID unique (role_id);
alter table users add constraint UK_USERS_EMAIL unique (email);

alter table sessions add constraint FK_SESSIONS_USER_ID foreign key (user_id) references users (id);
alter table users add constraint FK_USERS_ROLE_ID foreign key (role_id) references roles (id);