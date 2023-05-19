insert into _user (_id, email, password, role)
values ('some_id', 'some_email@some_domain.com', 'admin', 'admin');

insert into device (id, user_id, email)
values ('device_manual_tests', 'some_id', 'some_email@some_domain.com')