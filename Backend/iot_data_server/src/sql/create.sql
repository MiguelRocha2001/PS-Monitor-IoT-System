create table _user(
                      _id int primary key,
                      username varchar,
                      password varchar,
                      email varchar unique,
                      mobile varchar unique
);

create table token(
                      user_id serial primary key,
                      token varchar
);

create table device(
    id varchar primary key
);