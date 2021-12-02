CREATE TABLE user_info (

    user_id text NOT NULL ,
    name text NOT NULL,
    email text NOT NULL,
    mobile_number numeric NOT NULL,
    address text NOT NULL,
    CONSTRAINT user_info_pkey PRIMARY KEY (user_id)
) 
TABLESPACE pg_default;