CREATE TABLE buyer_info (

    buyer_id text NOT NULL ,
    buyer_name text NOT NULL,
    email text NOT NULL,
    mobile_number numeric NOT NULL,
    address text NOT NULL,
    book_name text NOT NULL,
    book_id text NOT NULL ,
    author text NOT NULL,
    price text NOT NULL ,
    quantity int,
    id text NOT NULL ,
    created_time text NOT NULL,
    CONSTRAINT buyer_info_pkey PRIMARY KEY (buyer_id)
    
) 
TABLESPACE pg_default;