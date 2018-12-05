-- Uitdagingen ophalen
set @username = 'rik';

SELECT 
    username_player1, answer_player2
FROM
    game
WHERE
    username_player2 = @username
        AND answer_player2 = 'unknown';

-- Uitdaging accepteren

set @username = 'rik';
set @gameid = 508;

UPDATE game 
SET 
    answer_player2 = 'accepted'
WHERE
    username_player2 = @username
        AND game_id = @gameid;

-- Uitdaging weigeren

set @username = 'rik';
set @gameid = 508;

UPDATE game 
SET 
    answer_player2 = 'rejected'
WHERE
    username_player2 = @username
        AND game_id = @gameid;

-- Speler uitdaging aanmaken

set @player1 = 'ger';
set @player2 = 'rik';

INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2)
VALUES ('request','NL',@player1,@player2,'unknown');

-- Spelers ophalen

set @username = 'ger';

SELECT 
    account.username
FROM
    account
        LEFT JOIN
    game ON account.username = game.username_player2
        JOIN
    accountrole ON account.username = accountrole.username
WHERE
    game.username_player2 IS NULL
        AND account.username <> @username
        AND accountrole.role = 'player';
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Woord suggesties ophalen

SELECT 
    *
FROM
    dictionary
WHERE
    state = 'pending';

-- Woord suggestie toevoegen

set @username = 'rik';

INSERT INTO dictionary
VALUES ('testwoordje','NL','pending',@username);

-- Woord suggestie accepteren

set @word = 'testwoordje';

UPDATE dictionary
SET state = 'accepted'
WHERE word = @word;

-- Woord suggestie weigeren

set @word = 'testwoordje';

UPDATE dictionary
SET state = 'denied'
WHERE word = @word;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Login check

set @username = 'ger';
set @upassword = '123';

SELECT 
    username
FROM
    account
WHERE
    username = @username
        AND password = @upassword;
        
-- Account regristeren

set @username = 'testmannetje';
set @upassword = 'test';

if not exists(select username from account where username = @username)
begin

end

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
select * from game where answer_player2 = 'unknown';
select * from answer;
select * from gamestate;
select * from role;
select * from wordstate;
select * from account;