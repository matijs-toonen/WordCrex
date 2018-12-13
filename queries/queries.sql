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

