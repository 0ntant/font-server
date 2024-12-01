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
