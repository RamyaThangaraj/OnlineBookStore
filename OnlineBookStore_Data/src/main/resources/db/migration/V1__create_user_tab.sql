CREATE TABLE users (

    id text NOT NULL ,
    username text NOT NULL,
    password text NOT NULL,
    email text NOT NULL,
    mobile_number numeric NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT email_unique_key UNIQUE (email)

) 
TABLESPACE pg_default;


CREATE TABLE roles
(
    id text NOT NULL,
    name text NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;
    

CREATE TABLE user_roles
(
    user_id text NOT NULL,
    role_id text NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT role_foreign_key FOREIGN KEY (role_id)
        REFERENCES roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_foreign_key FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

