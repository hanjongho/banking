insert into account (account_no, amount) values ('264-167-156072', 100000);
insert into account (account_no, amount) values ('237-697-281460', 0);

insert into member (login_id, password, activated, account_id) values ('numble-hanjongho', '$2a$10$Z.TSAwpQEA3ErAlbanSf9eb9lZkhjy0NtaoSNjFoi.CvWJvAEnhW6', 1, 1);
insert into member (login_id, password, activated, account_id) values ('numble-tester', '$2a$10$BqEiVYY/uQgIX4dBekhdCu2zr0eMMHprcAGnzjXIJuen7n./QMOAK', 1, 2);

insert into authority (authority_name) values ('ROLE_MEMBER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into member_authority (id, authority_name) values (1, 'ROLE_MEMBER');
insert into member_authority (id, authority_name) values (1, 'ROLE_ADMIN');
insert into member_authority (id, authority_name) values (2, 'ROLE_MEMBER');
