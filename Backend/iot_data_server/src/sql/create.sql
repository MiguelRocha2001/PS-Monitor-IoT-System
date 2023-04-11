create table _user(
    _id int primary key,
    username varchar,
    password varchar,
    email varchar unique,
);

create table token(
    user_id serial primary key,
    token varchar,
    iv varchar
);

create table device(
    id varchar primary key,
    email varchar,
);

create table salt(
    salt varchar primary key,
    user_id int
);