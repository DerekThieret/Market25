BEGIN TRANSACTION;

DROP TABLE IF EXISTS users ;
DROP TABLE IF EXISTS game ;
DROP TABLE IF EXISTS invite;
DROP TABLE IF EXISTS game_data ;
DROP TABLE IF EXISTS account ;
DROP TABLE IF EXISTS stock_amount ;
DROP TABLE IF EXISTS stock_values ;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;


CREATE TABLE users (
	user_id int DEFAULT nextval('seq_user_id'::regclass) NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	CONSTRAINT PK_users PRIMARY KEY (user_id)

);

CREATE TABLE game (
    game_id serial,
    organizer_id int NOT NULL,
    game_name varchar (40) NOT NULL,
    start_date DATE NOT NULL,
    -- CHECK THIS CONSTRAINT IF IT DOESN'T WORK!
    end_date DATE,
	--  CHECK end_date > start_date NOT NULL,
    CONSTRAINT PK_game PRIMARY KEY (game_id)
    -- May not need below.
    -- CONSTRAINT FK_game_users FOREIGN KEY organizer_id REFERENCES users(user_id)
);

CREATE TABLE invite (
    user_id int NOT NULL,
    game_id int NOT NULL,
    accepted boolean DEFAULT false,
    CONSTRAINT FK_invite_users FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT FK_invite_game FOREIGN KEY (game_id) REFERENCES game(game_id)

);

CREATE TABLE account (
    account_id serial,
    stock_value DECIMAL(19,2) NOT NULL,
    dollar_amount DECIMAL(19,2) NOT NULL,
    user_balance DECIMAL(19,2),
    CONSTRAINT PK_account PRIMARY KEY (account_id)

);


CREATE TABLE game_data (
    game_id int  NOT NULL,
    user_id int  NOT NULL,
    account_id int  NOT NULL,
    CONSTRAINT PK_game_data PRIMARY KEY (game_id, user_id, account_id),
    CONSTRAINT FK_game_data_account FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT FK_game_data_users FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT FK_game_data_game FOREIGN KEY (game_id) REFERENCES game(game_id)
);

CREATE TABLE stock_price (
    stock_symbol varchar(10) NOT NULL,
    stock_price DECIMAL(19,2) NOT NULL,
    CONSTRAINT PK_stock_price PRIMARY KEY (stock_symbol)

);

CREATE TABLE stock_amount (
    account_id int NOT NULL,
    stock_symbol varchar(10) NOT NULL,
    total_shares DECIMAL(19,2) NOT NULL,
    CONSTRAINT FK_stock_amount_account FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT FK_stock_amount_stock_symbol FOREIGN KEY (stock_symbol) REFERENCES stock_price(stock_symbol)
);


-- added above

INSERT INTO users (email,username,password_hash,role) VALUES ('foo@game.com','user','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_USER');
INSERT INTO users (email,username,password_hash,role) VALUES ('foo@game.com','admin','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_ADMIN');

INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('MSFT', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('AAPL', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('AMZN', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('GOOGL', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('BABA', '0');

INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('FB', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('VOD', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('V', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('JPM', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('MA', '0');

INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('TSM', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('CHT', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('UNH', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('HD', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('INTC', '0');

INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('KO', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('DIS', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('NVS', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('NFLX', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('ORCL', '0');

INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('NVDA', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('TM', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('CVX', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('ADBE', '0');
INSERT INTO stock_price (stock_symbol,stock_price) VALUES ('CSCO', '0');


COMMIT TRANSACTION;
