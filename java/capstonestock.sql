SELECT * FROM users u JOIN gamedata gd ON
                u.user_id = gd.user_id JOIN game g ON gd.game_id
                 = g.game_id WHERE u.user_id = 1;
                 
                 INSERT INTO game (game_name, organizer_id, start_date, end_date)
                 VALUES ('testgame' , 1 , '2022/02/01', '2022/02/02') RETURNING game_id;
                 
                 INSERT INTO account (user_balance, stock_value, dollar_amount)
                 VALUES (100000, 0, 100000) RETURNING account_id;
                 
                  INSERT INTO game_data (game_id, user_id, account_id) VALUES (1, 2, 1);
                 
                
                 
                 
                 
