CREATE TABLE transaction_fee
(
    id text  NOT NULL,
    fee text NOT NULL,
    CONSTRAINT transaction_fee_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

    
--INSERT INTO transaction_fee (id,fee) VALUES ('a0540ff0-7fff-4a59-a854-34d2655e3245','1');
    