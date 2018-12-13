-- Uitdagingen ophalen
set @username = 'rik';

SELECT 
    username_player1, username_player2, answer_player2
FROM
    game
WHERE
    username_player2 = @username
        OR username_player1 = @username
        AND answer_player2 = 'unknown';
        

-- Uitdaging accepteren

set @username = 'luc';
set @gameid = 503;

UPDATE game 
SET 
    answer_player2 = 'accepted',
    game_state = 'playing'
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

set @player1 = 'rik';
set @player2 = 'allrights';

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
    
-- Woord suggestie per gebruiker

set @username = 'rik';

SELECT 
    *
FROM
    dictionary
WHERE
    state = 'pending'
		AND username = @username;

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

INSERT INTO account (username, password)
SELECT * FROM (SELECT @username, @upassword) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM account WHERE username = @username
) LIMIT 1;

insert into accountrole (username, role)
values (@username,'player');
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Woord checken

set @wordGuess = 'camping';

select word 
from dictionary
where word = @wordGuess;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Bord
-- Turn aanmaken(elke keer als een turn is afgelopen de turn id ophogen)

set @gameid = 500;

INSERT INTO turn (game_id, turn_id)
VALUES( @gameid, (select max(turn_id) + 1 from turn where game_id = @gameid));

INSERT INTO turnplayer1 (game_id, turn_id, username_player1, turnaction_type)
VALUES( @gameid, (select max(turn_id) + 1 from turn where game_id = @gameid), 'ger', 'play');

INSERT INTO turnplayer2 (game_id, turn_id, username_player2, turnaction_type)
VALUES( @gameid, (select max(turn_id) + 1 from turn where game_id = @gameid), 'rik', 'play');

-- turn acties verwerken
-- Als er gewoon gespeeld word

set @gameid = 500;

update turnplayer1
set turnaction_type = 'play'
where username_player1 = 'ger' and game_id = @gameid;

update turnplayer2
set turnaction_type = 'play'
where username_player2 = 'rik' and game_id = @gameid;

-- Als iemand de beurt passt
set @gameid = 500;

update turnplayer1
set turnaction_type = 'play'
where username_player1 = 'ger' and game_id = @gameid;

update turnplayer2
set turnaction_type = 'pass'
where username_player2 = 'rik' and game_id = @gameid and turn_ID = (select max(turn_id) from turn where game_id = @gameid);
-- check of er een pass aanwezig is in de huidige turn
set @gameid = 500;


UPDATE game 
SET 
    game_state = 'resigned'
WHERE
    game_id = (SELECT 
            g.game_id
        FROM
            game AS g
                JOIN
            turnplayer1 AS p1 ON p1.game_id = g.game_id
                JOIN
            turnplayer2 AS p2 ON p2.game_id = g.game_id
        WHERE
            p1.turnaction_type = 'resign'
                OR p2.turnaction_type = 'resign'
                AND g.game_id = 500); 

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --



select * from game where answer_player2 = 'unknown';
select * from answer;
select * from gamestate;
select * from role;
select * from wordstate;
select * from account;
select * from dictionary where word like 't%';
select * from turn where game_id = 500;