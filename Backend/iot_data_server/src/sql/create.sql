create table _user(
    _id varchar primary key,
    username varchar unique,
    password varchar,
    email varchar unique
);

create table token(
    user_id varchar primary key,
    token varchar,
    iv varchar
);

create table device(
    id varchar primary key,
    user_id varchar,
    email varchar,
    foreign key (user_id) references _user(_id)
);

create table salt(
    salt varchar primary key,
    user_id varchar
);