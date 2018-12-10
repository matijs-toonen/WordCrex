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


-- gebruikers beherenqqqqqqqqqqqqqq

SELECT 
    *
FROM
    accountrole;


SET @username = 'jagermeester';
SET @role = 'player';


INSERT INTO accountrole values(@username,@role);


-- instellingen 
SET @username = 'jagermeester';


SELECT 
    username, role
FROM
    accountrole
WHERE
    username = @username;
    
    
    
SET @usernamee = 'jagermeester';    
SET @wachtwoord = 'P@ssw0rd'; 


UPDATE `account`
SET `password` = @wachtwoord
WHERE username = @usernamee;



-- history bader

select * from gelegdplayer1;

select * from gelegdplayer2;

select * from gelegd;


select tp1.game_id,tp1.turn_id, tp1.username_player1, tp1.score+tp1.bonus as score , gp1.woorddeel 
from turnplayer1 as tp1
inner join gelegdplayer1 as gp1
on tp1.game_id = gp1.game_id
and 
tp1.turn_id = gp1.turn_id;


select tp2.game_id,tp2.turn_id, tp2.username_player2, tp2.score+tp2.bonus as score, gp2.woorddeel 
from turnplayer2 as tp2
inner join gelegdplayer2 as gp2
on tp2.game_id = gp2.game_id
and 
tp2.turn_id = gp2.turn_id;


-- -- -- -- -- -- -- -- -- -- -- -- 

SELECT 
    tp1.game_id,
    tp1.turn_id,
    tp1.username_player1,
    tp1.score + tp1.bonus AS score,
    gp1.woorddeel,
    tp2.username_player2,
    tp2.score + tp2.bonus AS score,
    gp2.woorddeel
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
        AND tp2.turn_id = gp2.turn_id;
;

select * from turn;

select * from turnplayer1;

select * from turnplayer2;

