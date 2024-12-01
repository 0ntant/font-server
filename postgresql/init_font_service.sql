DROP DATABASE IF EXISTS shapy_db;

CREATE DATABASE shapy_db;
\c shapy_db


DROP TABLE IF EXISTS font_family_has_tag;
DROP TABLE IF EXISTS sh_user_has_role;
DROP TABLE IF EXISTS sh_user_has_project;
DROP TABLE IF EXISTS sh_user_tokens;

DROP TABLE IF EXISTS font;  
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS font_family;
DROP TABLE IF EXISTS tag_category;
DROP TABLE IF EXISTS sh_roles;
DROP TABLE IF EXISTS sh_users;
DROP TABLE IF EXISTS project;


CREATE USER shapy_app  WITH PASSWORD '+uUL9u@0T4!qCv6%~';

CREATE TABLE tag_category
(
    id  SERIAL PRIMARY KEY,
    title varchar(255) UNIQUE
);

CREATE TABLE font_family
(
    id SERIAL PRIMARY KEY,
    title varchar(255) UNIQUE
);

CREATE TABLE tag
(
    id SERIAL PRIMARY KEY,
    title varchar(255),
    friendly_name varchar(255),
    tag_category_id integer NOT NULL,

    FOREIGN KEY(tag_category_id) REFERENCES tag_category(id)
);

CREATE TABLE font
(
    id SERIAL PRIMARY KEY,
    type varchar(255) NOT NULL,
    file_path varchar(255) NOT NULL UNIQUE,
    font_family_id integer,
    is_corrupted boolean NOT NULL,

    FOREIGN KEY(font_family_id) REFERENCES font_family(id)
);

CREATE TABLE font_family_has_tag
(
    font_family_id integer NOT NULL,
    tag_id integer NOT NULL,

    FOREIGN KEY(font_family_id) REFERENCES font_family(id),
    FOREIGN KEY(tag_id) REFERENCES tag(id)
);

CREATE TABLE project 
(
	id SERIAL PRIMARY KEY,
	title varchar(255),
	thumbnail_path varchar(255),
	
	create_date TIMESTAMP, 
	modify_date TIMESTAMP	
);

CREATE TABLE sh_roles 
(
	id SERIAL PRIMARY KEY,
	title varchar(255) NOT NULL UNIQUE
);


CREATE TABLE sh_users
(
	id SERIAL PRIMARY KEY,
	
	password varchar(255) NOT NULL,
	username varchar(255) NOT NULL UNIQUE,
	email varchar(255) NOT NULL UNIQUE, 
	avatar_path varchar(255) UNIQUE, 
	
	is_enabled boolean NOT NULL,
	is_account_non_locked boolean NOT NULL,
	is_account_non_expired boolean NOT NULL,
	is_credentials_non_expired boolean NOT NULL,
	
	create_date TIMESTAMP, 
	modify_date TIMESTAMP	
);



CREATE TABLE sh_user_tokens
(
	 user_id SERIAL PRIMARY KEY REFERENCES sh_users(id),
   
	 jwt_refresh varchar(255)
);

CREATE TABLE sh_user_has_role
(
	user_id integer NOT NULL,
    role_id integer NOT NULL,

    FOREIGN KEY(user_id) REFERENCES sh_users(id),
    FOREIGN KEY(role_id) REFERENCES sh_roles(id)
);

CREATE TABLE sh_user_has_project
(
	user_id integer NOT NULL,
	project_id integer NOT NULL,
	
	FOREIGN KEY (user_id) REFERENCES sh_users(id),
	FOREIGN KEY (project_id) REFERENCES project(id) 
);


GRANT SELECT, UPDATE, DELETE, INSERT 
ON 
	font, 
	font_family, 
	tag, 
	font_family_has_tag, 
	tag_category,
	sh_roles,
	sh_users,
	sh_user_has_role,
	project,
	sh_user_has_project,
	sh_user_tokens
TO shapy_app;

GRANT ALL ON ALL SEQUENCES 
IN SCHEMA public
TO shapy_app;

INSERT INTO tag_category(title) VALUES
    ('Numbers'),
    ('Letterforms'),
    ('Glyph width'),
    ('Stylistic sets'),
    ('Spacing'),
    ('Positioning'),
    ('Character Variants');

INSERT INTO sh_roles (title) VALUES
('ROLE_CLIENT'),
('ROLE_MANAGER'),
('ROLE_ADMIN'),
('ROLE_DEVELOPER');

