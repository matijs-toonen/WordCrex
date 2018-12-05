-- Games
-- klaar en winnaar


SET @username = 'jagermeester';

SELECT 
    game_id,
    (SELECT 
            IF(username_player1 = @username,
                    username_player2,
                    username_player1)
        ) AS 'username_player2',
    game_state,
    (SELECT 
            IF(score1 > score2,
                    username_player1,
                    username_player2)
        ) AS 'username_player1'
FROM
    score
WHERE
	(game_state = 'finished' OR game_state = 'resigned')
	AND 
    (username_player1 = @username OR username_player2 = @username);
    
    
    
-- game

select count(turn_id)
into @player1
from turnplayer1;


select count(turn_id)
into @player2
from turnplayer2;



select if(@player1 = @player2,)



