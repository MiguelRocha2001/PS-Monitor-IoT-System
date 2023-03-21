create table _user(
    id int primary key,
    username varchar,
    password varchar,
    email varchar,
    mobile varchar
);

create table token(
    user_id serial primary key,
    token varchar
);

create table device(
    id varchar primary key
);

create table ph_record(
    device_id varchar,
    time timestamp,
    value float
);

create table temperature_record(
    device_id varchar,
    time timestamp,
    value float
);