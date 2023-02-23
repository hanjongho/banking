insert into member (login_id, password, activated) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 1);
insert into member (login_id, password, activated) values ('user', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 1);

insert into authority (authority_name) values ('ROLE_MEMBER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into member_authority (id, authority_name) values (1, 'ROLE_MEMBER');
insert into member_authority (id, authority_name) values (1, 'ROLE_ADMIN');
insert into member_authority (id, authority_name) values (2, 'ROLE_MEMBER');