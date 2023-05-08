insert into _user (_id, username, password, email)
values ('some_id', 'admin', 'admin', 'some_email@some_domain.com')

insert into device (id, user_id, email)
values ('device_manual_tests', 'some_id', 'some_email@some_domain.com')