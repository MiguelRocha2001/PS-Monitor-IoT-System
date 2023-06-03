insert into _user (_id, email, role)
values ('admin-id', 'admin_email@gmail.com', 'admin');

insert into password (user_id, value, salt)
values ('admin-id', 'admin-password', 'admin-salt');

insert into device (id, user_id, email)
values ('device_manual_tests', 'some_id', 'some_email@some_domain.com')