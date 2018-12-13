

-- start game
-- het uitnodige van een andere speler
set @usernameUitnodiger = 'jagermeester';
set @usernameUitgenodigde = 'Lidewij';


INSERT INTO game ( `game_state`, `letterset_code`, `username_player1`,`username_player2`,`answer_player2` )
VALUES  ("request", "NL", @usernameUitnodige, @usernameUitgenodigde, "unknown");

select * from hand;


-- 


-- hand has letter + score
SELECT * 
FROM handletter 
NATURAL JOIN letter 
NATURAL JOIN symbol 
where game_id =  500
AND turn_id = (select max(turn_id)
from handletter
where game_id = 500);



-- BORD

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



-- make playerturn and insert word

INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type)
VALUES ( @gameid, 1, 'Lidewij', 0, 96, 'play');

INSERT INTO boardplayer1 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 'jagermeester', 1, 35, 6, 8),
(@gameid, 'jagermeester', 1, 51, 7, 8),
(@gameid, 'jagermeester', 1, 17, 8, 8),
(@gameid, 'jagermeester', 1, 82, 9, 8);


-- update score if score = gelijk


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

-- als hij gelijk is 

UPDATE turnplayer1 
SET bonus = 5 
where turn_id = (select max(turn_id) from turnplayer1);

UPDATE turnplayer2
SET bonus = 5 
where turn_id = (select max(turn_id) from turnplayer2);











