-- Games
-- klaar en winnaar

SET @username = 'jagermeester';

SELECT 
    game_id,
    (SELECT 
            IF(username_player1 = @username,
                    username_player2,
                    username_player1)
        ) AS 'tegenstander',
    game_state,
    (SELECT 
            IF(score1 > score2,
                    username_player1,
                    username_player2)
        ) AS 'winner'
FROM
    score
WHERE
	(game_state = 'finished' OR game_state = 'resigned')
	AND 
    (username_player1 = @username OR username_player2 = @username);
    
    
-- na de update

SET @username = 'jagermeester';

SELECT 
    game_id,
    username_winner,
    (SELECT 
            IF(username_player1 = @username,
                    username_player2,
                    username_player1)
        ) AS 'opponent'
FROM
    game
WHERE
    (username_player1 = @username
        OR username_player2 = @username)
        AND (username_winner IS NOT NULL)
;


-- actieve games


SET @username = 'jagermeester';


SELECT 

	(SELECT 
            IF(g.username_player1 = @username,
                    g.username_player2,
                    g.username_player1)
        ) AS 'opponent',
	g.game_id,
    g.game_state,
    MAX(tp1.turn_id) AS max_turn_p1_zet,
    tp1.username_player1 AS max_turn_p1_username,
    
    MAX(tp2.turn_id) AS max_turn_p2_zet,
    tp2.username_player2 AS max_turn_p2_username
    
FROM
    game g
        LEFT JOIN
    turnplayer1 tp1 ON g.game_id = tp1.game_id
        LEFT JOIN
    turnplayer2 tp2 ON g.game_id = tp2.game_id
    
where (g.username_player1 = @username
        OR g.username_player2 = @username)
        and
        (g.game_state = 'playing')
GROUP BY g.game_id;


select * from game;



-- gebruikers beherenqqqqqqqqqqqqqq

SELECT 
    *
FROM
    accountrole;


SET @username = 'jagermeester';
SET @role = 'player';


INSERT INTO accountrole values(@username,@role);


-- instellingen 
--
SET @username = 'jagermeester';


SELECT 
    username, role
FROM
    accountrole
WHERE
    username = @username;
--
    
    
SET @usernamee = 'jagermeester';    
SET @wachtwoord = 'P@ssw0rd'; 


UPDATE `account`
SET `password` = @wachtwoord
WHERE username = @usernamee;



-- history bader

-- -- -- -- -- -- -- -- -- -- -- -- 
SELECT 
    tp1.game_id,
    tp1.turn_id,
    tp1.username_player1 AS player1,
    tp1.score + tp1.bonus AS score1,
    gp1.woorddeel AS woorddeel1,
    tp2.username_player2 AS player2,
    tp2.score + tp2.bonus AS score2,
    gp2.woorddeel AS woorddeel2
FROM
    turnplayer1 AS tp1
        INNER JOIN
    gelegdplayer1 AS gp1 ON tp1.game_id = gp1.game_id
        AND tp1.turn_id = gp1.turn_id
        INNER JOIN
    turnplayer2 AS tp2 ON tp1.game_id = tp2.game_id
        AND tp1.turn_id = tp2.turn_id
        INNER JOIN
    gelegdplayer2 AS gp2 ON tp2.game_id = gp2.game_id
        AND tp2.turn_id = gp2.turn_id
WHERE
    tp1.game_id = 502;


-- BORD

-- hand has letter + score (matijs)
SELECT * 
FROM handletter 
NATURAL JOIN letter 
NATURAL JOIN symbol 
where game_id =  500
AND turn_id = (select max(turn_id)
from handletter
where game_id = 500);


-- kijken wie er de beurt aan is op basis van game_id

SELECT 
	g.game_id,
    g.game_state,
    MAX(tp1.turn_id) AS max_turn_p1_zet,
    tp1.username_player1 AS max_turn_p1_username,
    
    MAX(tp2.turn_id) AS max_turn_p2_zet,
    tp2.username_player2 AS max_turn_p2_username
    
FROM
    game g
        LEFT JOIN
    turnplayer1 tp1 ON g.game_id = tp1.game_id
        LEFT JOIN
    turnplayer2 tp2 ON g.game_id = tp2.game_id
    
where (g.game_id = 502)
GROUP BY g.game_id;



-- een zet zetten

INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type)
VALUES ( @gameid, 1, 'Lidewij', 0, 96, 'play');

INSERT INTO boardplayer1 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 'jagermeester', 1, 35, 6, 8),
(@gameid, 'jagermeester', 1, 51, 7, 8),
(@gameid, 'jagermeester', 1, 17, 8, 8),
(@gameid, 'jagermeester', 1, 82, 9, 8);


-- kijken of de score gelijk is tussen de spelers

SELECT 
	g.game_id,
    g.game_state,
    MAX(tp1.turn_id) AS max_turn_p1_zet,
    tp1.username_player1 AS max_turn_p1_username,
    tp1.score as max_turn_p1_score,
    
    MAX(tp2.turn_id) AS max_turn_p2_zet,
    tp2.username_player2 AS max_turn_p2_username,
    tp2.score as max_turn_p2_score
    
FROM
    game g
        LEFT JOIN
    turnplayer1 tp1 ON g.game_id = tp1.game_id
        LEFT JOIN
    turnplayer2 tp2 ON g.game_id = tp2.game_id
    
where (g.game_id = 502)
GROUP BY g.game_id;

-- als de score gelijk is 5 punten toevoegen aan bonus
UPDATE turnplayer1 
SET bonus = 5 
where turn_id = (select max(turn_id) from turnplayer1);

UPDATE turnplayer2
SET bonus = 5 
where turn_id = (select max(turn_id) from turnplayer2);

-- 

-- als de game klaar is the winnar bepalen


SET @gameid = 502;

SELECT 
    game_id,
    game_state,
    (SELECT 
            IF(score1 > score2,
                    username_player1,
                    username_player2)
        ) AS 'winner'
FROM
    score
WHERE
    (game_state = 'finished'
        OR game_state = 'resigned')
        AND (game_id = 502);

-- history davod

SELECT 
    tp1.game_id,
    tp1.turn_id,
    tp1.username_player1 AS player1,
    tp1.score + tp1.bonus AS score1,
    gp1.woorddeel AS woorddeel1,
    tp2.username_player2 AS player2,
    tp2.score + tp2.bonus AS score2,
    gp2.woorddeel AS woorddeel2,
    H.inhoud
FROM
    turnplayer1 AS tp1
        INNER JOIN
    gelegdplayer1 AS gp1 ON tp1.game_id = gp1.game_id
        AND tp1.turn_id = gp1.turn_id
        INNER JOIN
    turnplayer2 AS tp2 ON tp1.game_id = tp2.game_id
        AND tp1.turn_id = tp2.turn_id
        INNER JOIN
    gelegdplayer2 AS gp2 ON tp2.game_id = gp2.game_id
        AND tp2.turn_id = gp2.turn_id
        inner join hand as H
        on H.game_id =  tp1.game_id
        and H.turn_id = tp1.turn_id
WHERE
    tp1.game_id = 502;



select * from hand;


-- unused letters

SELECT * FROM letter where game_id = 503 and letter_id NOT IN (SELECT letter_id FROM handletter where game_id = 503); 

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

-- resigned
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

set @gameid = 502;
set @turnid = 1;

select letter_id,game_id,turn_id,tile_x,tile_y
from boardplayer1
where game_id = @gameid and turn_id = @turnid;

select letter_id,game_id,turn_id,tile_x,tile_y
from boardplayer2
where game_id = @gameid and turn_id = @turnid;

delete from turn where game_id = 513;
delete from game where game_id = 513;
select * from game where answer_player2 = 'unknown';
select * from answer;
select * from gamestate;
select * from role;
select * from wordstate;
select * from account;
select * from dictionary where word like 't%';
select * from turn where game_id = 500;