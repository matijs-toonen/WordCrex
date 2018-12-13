

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


-- kijken wie er de beurt aan is



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


