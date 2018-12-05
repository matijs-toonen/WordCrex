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


-- gebruikers beheren

SELECT 
    *
FROM
    accountrole;


SET @username = 'jagermeester';
SET @role = 'player';


INSERT INTO accountrole values(@username,@role);

