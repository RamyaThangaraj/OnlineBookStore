CREATE TABLE book_info (

    book_id text NOT NULL ,
    bookname text NOT NULL,
    author text NOT NULL,
    price text NOT NULL,
    no_of_book_available int,
    CONSTRAINT book_info_pkey PRIMARY KEY (book_id),
    CONSTRAINT bookname_unique_key UNIQUE (bookname)

) 
TABLESPACE pg_default;