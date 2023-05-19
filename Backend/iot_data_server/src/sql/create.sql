-- TODO: define constraints in here

create table _user(
    _id varchar primary key,
    email varchar unique,
    password varchar,
    role varchar
);

create table token(
    user_id varchar primary key,
    token varchar,
    iv varchar,
    foreign key (user_id) references _user(_id)
);

create table password(
    user_id varchar primary key,
    value varchar,
    salt varchar,
    foreign key (user_id) references _user(_id)
);

create table device(
    id varchar primary key,
    user_id varchar,
    email varchar,
    foreign key (user_id) references _user(_id)
);

create table sensor(
    type varchar,
    alert_threshold int,
    primary key (type)
);

create table sensor_error(
    device_id varchar,
    sensor varchar,
    timestamp timestamp,
    primary key (device_id, sensor, timestamp),
    foreign key (device_id) references device(id)
);

create table device_wake_up_log(
    device_id varchar,
    timestamp timestamp,
    reason varchar,
    primary key (device_id, timestamp),
    foreign key (device_id) references device(id)
);

create table verification_code(
    code varchar primary key,
    user_email varchar
);