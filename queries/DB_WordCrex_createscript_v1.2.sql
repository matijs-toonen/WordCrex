#######################################
# WORDCREX DATABASE                   #
# CREATESCRIPT                        #
# versie 1.2                          #
# VSOPRJ2 18/19                       #
# datum: 9-12-2018                    #
# (c) Ger Saris                       #
#######################################

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Table `account`
-- -----------------------------------------------------
CREATE TABLE `account` (
  `username` VARCHAR(25) NOT NULL,
  `password` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`username`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `gamestate`
-- -----------------------------------------------------
CREATE TABLE `gamestate` (
  `state` VARCHAR(8) NOT NULL COMMENT 'Values:\nrequest\nplaying\nfinished\nresigned',
  PRIMARY KEY (`state`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `answer`
-- -----------------------------------------------------
CREATE TABLE `answer` (
  `type` VARCHAR(8) NOT NULL COMMENT 'Waarden:\nunknown\naccepted\nrejected',
  PRIMARY KEY (`type`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `letterset`
-- -----------------------------------------------------
CREATE TABLE `letterset` (
  `code` VARCHAR(2) NOT NULL,
  `description` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`code`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `game`
-- -----------------------------------------------------
CREATE TABLE `game` (
  `game_id` INT NOT NULL AUTO_INCREMENT,
  `game_state` VARCHAR(8) NOT NULL DEFAULT 'request',
  `letterset_code` VARCHAR(2) NOT NULL,
  `username_player1` VARCHAR(25) NOT NULL COMMENT 'challenger',
  `username_player2` VARCHAR(25) NOT NULL COMMENT 'opponent',
  `answer_player2` VARCHAR(8) NOT NULL DEFAULT 'unknown',
  `username_winner` VARCHAR(25) NULL,
  PRIMARY KEY (`game_id`),
  INDEX `fk_game_gamestate_idx` (`game_state` ASC),
  INDEX `fk_game_account1_idx` (`username_player1` ASC),
  INDEX `fk_game_account2_idx` (`username_player2` ASC),
  INDEX `fk_game_answer_idx` (`answer_player2` ASC),
  INDEX `fk_game_letterset_idx` (`letterset_code` ASC),
  INDEX `fk_game_account3_idx` (`username_winner` ASC),
  CONSTRAINT `fk_game_gamestate`
    FOREIGN KEY (`game_state`)
    REFERENCES `gamestate` (`state`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_account1`
    FOREIGN KEY (`username_player1`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_account2`
    FOREIGN KEY (`username_player2`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_answer`
    FOREIGN KEY (`answer_player2`)
    REFERENCES `answer` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_account3`
    FOREIGN KEY (`username_winner`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE `role` (
  `role` VARCHAR(15) NOT NULL COMMENT 'player\nobserver\nmoderator\nadministrator',
  PRIMARY KEY (`role`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `chatline`
-- -----------------------------------------------------
CREATE TABLE `chatline` (
  `username` VARCHAR(25) NOT NULL,
  `game_id` INT NOT NULL,
  `moment` TIMESTAMP NOT NULL,
  `message` TEXT NOT NULL,
  INDEX `fk_chatregel_account_idx` (`username` ASC),
  INDEX `fk_chatregel_spel_idx` (`game_id` ASC),
  PRIMARY KEY (`username`, `game_id`, `moment`),
  CONSTRAINT `fk_chatregel_account`
    FOREIGN KEY (`username`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_chatregel_spel`
    FOREIGN KEY (`game_id`)
    REFERENCES `game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `accountrole`
-- -----------------------------------------------------
CREATE TABLE `accountrole` (
  `username` VARCHAR(25) NOT NULL,
  `role` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`username`, `role`),
  INDEX `fk_accountrol_rol_idx` (`role` ASC),
  INDEX `fk_accountrol_account_idx` (`username` ASC),
  CONSTRAINT `fk_accountrol_rol`
    FOREIGN KEY (`username`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_accountrol_account`
    FOREIGN KEY (`role`)
    REFERENCES `role` (`role`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `tiletype`
-- -----------------------------------------------------
CREATE TABLE `tiletype` (
  `type` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`type`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `tile`
-- -----------------------------------------------------
CREATE TABLE `tile` (
  `x` INT NOT NULL,
  `y` INT NOT NULL,
  `tile_type` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`x`, `y`),
  INDEX `fk_tile_tiletype_idx` (`tile_type` ASC),
  CONSTRAINT `fk_tile_tiletype`
    FOREIGN KEY (`tile_type`)
    REFERENCES `tiletype` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `symbol`
-- -----------------------------------------------------
CREATE TABLE `symbol` (
  `letterset_code` VARCHAR(2) NOT NULL,
  `symbol` CHAR NOT NULL,
  `value` INT NOT NULL,
  `counted` INT NOT NULL,
  PRIMARY KEY (`letterset_code`, `symbol`),
  INDEX `fk_lettertype_letterset_idx` (`letterset_code` ASC),
  CONSTRAINT `fk_lettertype_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `letter`
-- -----------------------------------------------------
CREATE TABLE `letter` (
  `letter_id` INT NOT NULL,
  `game_id` INT NOT NULL,
  `symbol_letterset_code` VARCHAR(2) NOT NULL,
  `symbol` CHAR NOT NULL,
  PRIMARY KEY (`letter_id`, `game_id`),
  INDEX `fk_letter_symbol_idx` (`symbol_letterset_code` ASC, `symbol` ASC),
  INDEX `fk_letter_gamel_idx` (`game_id` ASC),
  CONSTRAINT `fk_letter_symbol`
    FOREIGN KEY (`symbol_letterset_code` , `symbol`)
    REFERENCES `symbol` (`letterset_code` , `symbol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_letter_game`
    FOREIGN KEY (`game_id`)
    REFERENCES `game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `turn`
-- -----------------------------------------------------
CREATE TABLE `turn` (
  `game_id` INT NOT NULL,
  `turn_id` INT NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`),
  INDEX `fk_turn_game_idx` (`game_id` ASC),
  CONSTRAINT `fk_turn_game`
    FOREIGN KEY (`game_id`)
    REFERENCES `game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `handletter`
-- -----------------------------------------------------
CREATE TABLE `handletter` (
  `game_id` INT NOT NULL,
  `turn_id` INT NOT NULL,
  `letter_id` INT NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`, `letter_id`),
  INDEX `fk_handletter_letter_idx` (`game_id` ASC, `letter_id` ASC),
  CONSTRAINT `fk_handletter_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `turnboardletter`
-- -----------------------------------------------------
CREATE TABLE `turnboardletter` (
  `letter_id` INT NOT NULL,
  `game_id` INT NOT NULL,
  `turn_id` INT NOT NULL,
  `tile_x` INT NOT NULL,
  `tile_y` INT NOT NULL,
  INDEX `fk_turnboardletter_tile_idx` (`tile_x` ASC, `tile_y` ASC),
  PRIMARY KEY (`letter_id`, `game_id`),
  UNIQUE INDEX `plaats_uniek` (`tile_x` ASC, `tile_y` ASC, `game_id` ASC),
  INDEX `fk_turnboardletter_turn_idx` (`game_id` ASC, `turn_id` ASC),
  CONSTRAINT `fk_turnboardletter_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_turnboardletter_letter`
    FOREIGN KEY (`letter_id` , `game_id`)
    REFERENCES `letter` (`letter_id` , `game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_turnboardletter_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `turnaction`
-- -----------------------------------------------------
CREATE TABLE `turnaction` (
  `type` VARCHAR(6) NOT NULL COMMENT 'Values:\nplay\npass\nresign\n',
  PRIMARY KEY (`type`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `wordstate`
-- -----------------------------------------------------
CREATE TABLE `wordstate` (
  `state` VARCHAR(8) NOT NULL,
  PRIMARY KEY (`state`))
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `dictionary`
-- -----------------------------------------------------
CREATE TABLE `dictionary` (
  `word` VARCHAR(15) NOT NULL,
  `letterset_code` VARCHAR(2) NOT NULL,
  `state` VARCHAR(8) NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`word`, `letterset_code`),
  INDEX `fk_woordenboek_woordstatus_idx` (`state` ASC),
  INDEX `fk_woordenboek_letterset_idx` (`letterset_code` ASC),
  INDEX `fk_woordenboek_account_idx` (`username` ASC),
  CONSTRAINT `fk_woordenboek_woordstatus`
    FOREIGN KEY (`state`)
    REFERENCES `wordstate` (`state`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_woordenboek_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_woordenboek_account`
    FOREIGN KEY (`username`)
    REFERENCES `account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `turnplayer1`
-- -----------------------------------------------------
CREATE TABLE `turnplayer1` (
  `game_id` INT NOT NULL,
  `turn_id` INT NOT NULL,
  `username_player1` VARCHAR(25) NOT NULL,
  `bonus` INT NOT NULL DEFAULT 0,
  `score` INT NOT NULL DEFAULT 0 COMMENT 'bonus NOT included',
  `turnaction_type` VARCHAR(6) NOT NULL,
  PRIMARY KEY (`turn_id`, `game_id`, `username_player1`),
  INDEX `fk_player1turn_turnaction_idx` (`turnaction_type` ASC),
  CONSTRAINT `fk_player1turn_turnaction`
    FOREIGN KEY (`turnaction_type`)
    REFERENCES `turnaction` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player1turn_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `turnplayer2`
-- -----------------------------------------------------
CREATE TABLE `turnplayer2` (
  `game_id` INT NOT NULL,
  `turn_id` INT NOT NULL,
  `username_player2` VARCHAR(25) NOT NULL,
  `bonus` INT NOT NULL DEFAULT 0,
  `score` INT NOT NULL DEFAULT 0,
  `turnaction_type` VARCHAR(6) NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`, `username_player2`),
  INDEX `fk_player2turn_turnaction_idx` (`turnaction_type` ASC),
  INDEX `fk_player2turn_turn_idx` (`game_id` ASC, `turn_id` ASC),
  CONSTRAINT `fk_player2turn_turnaction`
    FOREIGN KEY (`turnaction_type`)
    REFERENCES `turnaction` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player2turn_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `boardplayer2`
-- -----------------------------------------------------
CREATE TABLE `boardplayer2` (
  `game_id` INT NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  `turn_id` INT NOT NULL,
  `letter_id` INT NOT NULL,
  `tile_x` INT NOT NULL,
  `tile_y` INT NOT NULL,
  PRIMARY KEY (`game_id`, `username`, `turn_id`, `letter_id`),
  INDEX `fk_playerboardletter_tile_idx` (`tile_x` ASC, `tile_y` ASC),
  INDEX `fk_boardplayer2_letter1_idx` (`game_id` ASC, `letter_id` ASC),
  CONSTRAINT `fk_boardplayer2_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_boardplayer2_turnplayer2`
    FOREIGN KEY (`game_id` , `turn_id` , `username`)
    REFERENCES `turnplayer2` (`game_id` , `turn_id` , `username_player2`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;


-- -----------------------------------------------------
-- Table `boardplayer1`
-- -----------------------------------------------------
CREATE TABLE `boardplayer1` (
  `game_id` INT NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  `turn_id` INT NOT NULL,
  `letter_id` INT NOT NULL,
  `tile_x` INT NOT NULL,
  `tile_y` INT NOT NULL,
  PRIMARY KEY (`game_id`, `username`, `turn_id`, `letter_id`),
  INDEX `fk_boardplayer1_tile1_idx` (`tile_x` ASC, `tile_y` ASC),
  INDEX `fk_boardplayer1_turnplayer1_idx` (`turn_id` ASC, `game_id` ASC, `username` ASC),
  INDEX `fk_boardplayer1_letter1_idx` (`game_id` ASC, `letter_id` ASC),
  CONSTRAINT `fk_boardplayer1_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_boardplayer1_turnplayer1`
    FOREIGN KEY (`turn_id` , `game_id` , `username`)
    REFERENCES `turnplayer1` (`turn_id` , `game_id` , `username_player1`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = INNODB;



-- -----------------------------------------------------
-- View `score`
-- -----------------------------------------------------

CREATE VIEW `score` AS
SELECT g.game_id, g.`game_state`, g.`username_player1`, g.`username_player2`, 
	IFNULL((SELECT SUM(score) AS totaalscore FROM turnplayer1 t1
	WHERE t1.game_id = g.game_id),0) AS score1,
	IFNULL((SELECT SUM(bonus) totaalbonus FROM turnplayer1 t1
	WHERE t1.game_id = g.game_id),0) AS bonus1,
	IFNULL((SELECT SUM(score) AS totaalscore FROM turnplayer2 t2
	WHERE t2.game_id = g.game_id),0) AS score2,
	IFNULL((SELECT SUM(bonus) totaalbonus FROM turnplayer2 t2
	WHERE t2.game_id = g.game_id),0) AS bonus2
FROM game g
;

-- -----------------------------------------------------
-- View `gelegd`
-- -----------------------------------------------------

CREATE VIEW `gelegd` AS
SELECT tbl.game_id, tbl.turn_id,
       GROUP_CONCAT(symbol ORDER BY tile_x,tile_y) AS woorddeel,
       GROUP_CONCAT(tile_x ORDER BY tile_x,tile_y) AS `x-waarden`,
       GROUP_CONCAT(tile_y ORDER BY tile_x,tile_y) AS `y-waarden`
FROM turnboardletter tbl
JOIN letter l
ON l.game_id = tbl.game_id AND l.letter_id = tbl.letter_id
GROUP BY tbl.game_id, tbl.turn_id
;

-- -----------------------------------------------------
-- View `pot`
-- -----------------------------------------------------

CREATE VIEW `pot` AS
SELECT l.game_id AS game_id, l.letter_id, l.symbol 
FROM letter l                  -- geef alle letters
WHERE l.letter_id NOT IN       -- die niet
(  SELECT letter_id            -- gelegd zijn
   FROM turnboardletter tbl
   WHERE tbl.game_id = l.game_id )
AND l.letter_id NOT IN         -- en niet 
( SELECT letter_id             -- in de hand zitten
  FROM handletter hl
  WHERE hl.game_id = l.game_id
  AND hl.turn_id IN            -- tijdens 
  (SELECT MAX(t.turn_id)       -- de laatste turn
   FROM turn t
   WHERE t.game_id = l.game_id 
  )
)
ORDER BY l.game_id, l.letter_id
;

-- -----------------------------------------------------
-- View `hand`
-- -----------------------------------------------------

CREATE VIEW `hand` AS
SELECT hl.game_id, hl.turn_id,  
       GROUP_CONCAT(l.symbol ORDER BY l.symbol ) AS inhoud
FROM handletter hl
JOIN letter l
ON hl.game_id = l.game_id AND hl.letter_id = l.letter_id
JOIN turn t
ON hl.turn_id = t.turn_id AND hl.game_id = t.game_id
GROUP BY hl.game_id, hl.turn_id;

-- -----------------------------------------------------
-- View `gelegdplayer1`
-- -----------------------------------------------------

CREATE VIEW `gelegdplayer1` AS
SELECT pbl.game_id, pbl.turn_id,
       GROUP_CONCAT(symbol ORDER BY tile_x,tile_y) AS woorddeel,
       GROUP_CONCAT(tile_x ORDER BY tile_x,tile_y) AS `x-waarden`,
       GROUP_CONCAT(tile_y ORDER BY tile_x,tile_y) AS `y-waarden`
FROM boardplayer1 pbl
JOIN letter l
ON l.game_id = pbl.game_id AND l.letter_id = pbl.letter_id
GROUP BY pbl.game_id, pbl.turn_id
;

-- -----------------------------------------------------
-- View `gelegdplayer2`
-- -----------------------------------------------------

CREATE VIEW `gelegdplayer2` AS
SELECT pbl.game_id, pbl.turn_id,
       GROUP_CONCAT(symbol ORDER BY tile_x,tile_y) AS woorddeel,
       GROUP_CONCAT(tile_x ORDER BY tile_x,tile_y) AS `x-waarden`,
       GROUP_CONCAT(tile_y ORDER BY tile_x,tile_y) AS `y-waarden`
FROM boardplayer2 pbl
JOIN letter l
ON l.game_id = pbl.game_id AND l.letter_id = pbl.letter_id
GROUP BY pbl.game_id, pbl.turn_id
;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
