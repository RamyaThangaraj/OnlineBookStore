CREATE TABLE token_info
(
    id text NOT NULL,
   token_id text NOT NULL,
    status text NOT NULL,
    symbol text NOT NULL,
     PRIMARY KEY (id)
    
)

TABLESPACE pg_default;

CREATE TABLE user_security
(
    user_id text NOT NULL,
    account_shard numeric,
    account_realm numeric,
    account_num numeric,
    user_pub_key text ,
    user_private_key text ,
    account_balance text ,
    CONSTRAINT user_security_pkey PRIMARY KEY (user_id),
    CONSTRAINT user_sec_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

--INSERT INTO user_security(
--	user_id, account_shard, account_realm, account_num, user_pub_key, user_private_key, account_balance)
--	VALUES ('0b9f0359-66b3-4bb8-a075-2c1cec2c3472', 0, 0, 16720602, 
--	'302a300506032b65700321001f133ec1b6b9012e987018030b2f9ccc219403fa2c54a3c37dbee50c08f375ce', 
--	'302e020100300506032b65700422042071a82f3b5170c17f6b93e1c8f66e28e47216113e562210184a3b2a9089e7ab20', 10000);