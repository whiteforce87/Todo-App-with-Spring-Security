create schema new_schema123;

//This are for spring security tables:

create table users(username varchar_ignorecase(50) not null primary key,password varchar_ignorecase(500) not null,enabled boolean not null);
create table authorities (username varchar_ignorecase(50) not null,authority varchar_ignorecase(50) not null,constraint fk_authorities_users foreign key(username) references users(username));
create unique index ix_auth_username on authorities (username,authority);

//Creating todos for user:

insert into todo (id, description, done, target_date,username)
values (10001, 'Learn AWS', false, CURRENT_DATE(), 'fatih');

insert into todo (id, description, done, target_date,username)
values (10002, 'Get AWS Certified', false, CURRENT_DATE(), 'fatih');

insert into todo (id, description, done, target_date,username)
values (10003, 'Learn DevOps2', false, CURRENT_DATE(), 'fatih');

