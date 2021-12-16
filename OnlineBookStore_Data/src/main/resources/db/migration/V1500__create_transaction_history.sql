CREATE TABLE public.transaction_histroy
(
	id text NOT NULL,
    user_id text NOT NULL,
    date_time timestamp without time zone,
    transcation_type text,
    payments text,
    amount_in_currency text,
    stable_coin text,
    balance_stable_coin text,
    balance_amount_in_currency text,
    balance_hbars text,
    contract_id text,
    node_fee text,
    network_fee text,
    transaction_fee text,
    smart_contract_fee text,
    total_fee text,
    security_id text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES public.users (id)
	
)

TABLESPACE pg_default;
