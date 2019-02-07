-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
-- -----------------------------------------------------
-- Schema WordCrex
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema WordCrex
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `WordCrex` DEFAULT CHARACTER SET latin1 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `username` VARCHAR(16) NOT NULL,
  `email` VARCHAR(255) NULL,
  `password` VARCHAR(32) NOT NULL,
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP);

USE `WordCrex` ;

-- -----------------------------------------------------
-- Table `WordCrex`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`account` (
  `username` VARCHAR(25) NOT NULL,
  `password` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`role` (
  `role` VARCHAR(15) NOT NULL COMMENT 'player\\nobserver\\nmoderator\\nadministrator',
  PRIMARY KEY (`role`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`accountrole`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`accountrole` (
  `username` VARCHAR(25) NOT NULL,
  `role` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`username`, `role`),
  INDEX `fk_accountrol_rol_idx` (`role` ASC) VISIBLE,
  INDEX `fk_accountrol_account_idx` (`username` ASC) VISIBLE,
  CONSTRAINT `fk_accountrol_account`
    FOREIGN KEY (`role`)
    REFERENCES `WordCrex`.`role` (`role`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_accountrol_rol`
    FOREIGN KEY (`username`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`answer` (
  `type` VARCHAR(8) NOT NULL COMMENT 'Waarden:\\nunknown\\naccepted\\nrejected',
  PRIMARY KEY (`type`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`tiletype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`tiletype` (
  `type` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`type`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`tile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`tile` (
  `x` INT(11) NOT NULL,
  `y` INT(11) NOT NULL,
  `tile_type` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`x`, `y`),
  INDEX `fk_tile_tiletype_idx` (`tile_type` ASC) VISIBLE,
  CONSTRAINT `fk_tile_tiletype`
    FOREIGN KEY (`tile_type`)
    REFERENCES `WordCrex`.`tiletype` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`gamestate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`gamestate` (
  `state` VARCHAR(8) NOT NULL COMMENT 'Values:\\nrequest\\nplaying\\nfinished\\nresigned',
  PRIMARY KEY (`state`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`letterset`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`letterset` (
  `code` VARCHAR(2) NOT NULL,
  `description` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`code`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`game` (
  `game_id` INT(11) NOT NULL AUTO_INCREMENT,
  `game_state` VARCHAR(8) NOT NULL DEFAULT 'request',
  `letterset_code` VARCHAR(2) NOT NULL,
  `username_player1` VARCHAR(25) NOT NULL COMMENT 'challenger',
  `username_player2` VARCHAR(25) NOT NULL COMMENT 'opponent',
  `answer_player2` VARCHAR(8) NOT NULL DEFAULT 'unknown',
  `username_winner` VARCHAR(25) NULL DEFAULT NULL,
  PRIMARY KEY (`game_id`),
  INDEX `fk_game_gamestate_idx` (`game_state` ASC) VISIBLE,
  INDEX `fk_game_account1_idx` (`username_player1` ASC) VISIBLE,
  INDEX `fk_game_account2_idx` (`username_player2` ASC) VISIBLE,
  INDEX `fk_game_answer_idx` (`answer_player2` ASC) VISIBLE,
  INDEX `fk_game_letterset_idx` (`letterset_code` ASC) VISIBLE,
  INDEX `fk_game_account3_idx` (`username_winner` ASC) VISIBLE,
  CONSTRAINT `fk_game_account1`
    FOREIGN KEY (`username_player1`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_account2`
    FOREIGN KEY (`username_player2`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_account3`
    FOREIGN KEY (`username_winner`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_answer`
    FOREIGN KEY (`answer_player2`)
    REFERENCES `WordCrex`.`answer` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_gamestate`
    FOREIGN KEY (`game_state`)
    REFERENCES `WordCrex`.`gamestate` (`state`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_game_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `WordCrex`.`letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 527
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`turn`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`turn` (
  `game_id` INT(11) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`),
  INDEX `fk_turn_game_idx` (`game_id` ASC) VISIBLE,
  CONSTRAINT `fk_turn_game`
    FOREIGN KEY (`game_id`)
    REFERENCES `WordCrex`.`game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`turnaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`turnaction` (
  `type` VARCHAR(6) NOT NULL COMMENT 'Values:\\nplay\\npass\\nresign\\n',
  PRIMARY KEY (`type`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`turnplayer1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`turnplayer1` (
  `game_id` INT(11) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `username_player1` VARCHAR(25) NOT NULL,
  `bonus` INT(11) NOT NULL DEFAULT '0',
  `score` INT(11) NOT NULL DEFAULT '0' COMMENT 'bonus NOT included',
  `turnaction_type` VARCHAR(6) NOT NULL,
  PRIMARY KEY (`turn_id`, `game_id`, `username_player1`),
  INDEX `fk_player1turn_turnaction_idx` (`turnaction_type` ASC) VISIBLE,
  INDEX `fk_player1turn_turn` (`game_id` ASC, `turn_id` ASC) VISIBLE,
  CONSTRAINT `fk_player1turn_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `WordCrex`.`turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player1turn_turnaction`
    FOREIGN KEY (`turnaction_type`)
    REFERENCES `WordCrex`.`turnaction` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`boardplayer1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`boardplayer1` (
  `game_id` INT(11) NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `letter_id` INT(11) NOT NULL,
  `tile_x` INT(11) NOT NULL,
  `tile_y` INT(11) NOT NULL,
  PRIMARY KEY (`game_id`, `username`, `turn_id`, `letter_id`),
  INDEX `fk_boardplayer1_tile1_idx` (`tile_x` ASC, `tile_y` ASC) VISIBLE,
  INDEX `fk_boardplayer1_turnplayer1_idx` (`turn_id` ASC, `game_id` ASC, `username` ASC) VISIBLE,
  CONSTRAINT `fk_boardplayer1_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `WordCrex`.`tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_boardplayer1_turnplayer1`
    FOREIGN KEY (`turn_id` , `game_id` , `username`)
    REFERENCES `WordCrex`.`turnplayer1` (`turn_id` , `game_id` , `username_player1`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`turnplayer2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`turnplayer2` (
  `game_id` INT(11) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `username_player2` VARCHAR(25) NOT NULL,
  `bonus` INT(11) NOT NULL DEFAULT '0',
  `score` INT(11) NOT NULL DEFAULT '0',
  `turnaction_type` VARCHAR(6) NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`, `username_player2`),
  INDEX `fk_player2turn_turnaction_idx` (`turnaction_type` ASC) VISIBLE,
  INDEX `fk_player2turn_turn_idx` (`game_id` ASC, `turn_id` ASC) VISIBLE,
  CONSTRAINT `fk_player2turn_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `WordCrex`.`turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player2turn_turnaction`
    FOREIGN KEY (`turnaction_type`)
    REFERENCES `WordCrex`.`turnaction` (`type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`boardplayer2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`boardplayer2` (
  `game_id` INT(11) NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `letter_id` INT(11) NOT NULL,
  `tile_x` INT(11) NOT NULL,
  `tile_y` INT(11) NOT NULL,
  PRIMARY KEY (`game_id`, `username`, `turn_id`, `letter_id`),
  INDEX `fk_playerboardletter_tile_idx` (`tile_x` ASC, `tile_y` ASC) VISIBLE,
  INDEX `fk_boardplayer2_turnplayer2` (`game_id` ASC, `turn_id` ASC, `username` ASC) VISIBLE,
  CONSTRAINT `fk_boardplayer2_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `WordCrex`.`tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_boardplayer2_turnplayer2`
    FOREIGN KEY (`game_id` , `turn_id` , `username`)
    REFERENCES `WordCrex`.`turnplayer2` (`game_id` , `turn_id` , `username_player2`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`chatline`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`chatline` (
  `username` VARCHAR(25) NOT NULL,
  `game_id` INT(11) NOT NULL,
  `moment` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `message` TEXT NOT NULL,
  PRIMARY KEY (`username`, `game_id`, `moment`),
  INDEX `fk_chatregel_account_idx` (`username` ASC) VISIBLE,
  INDEX `fk_chatregel_spel_idx` (`game_id` ASC) VISIBLE,
  CONSTRAINT `fk_chatregel_account`
    FOREIGN KEY (`username`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_chatregel_spel`
    FOREIGN KEY (`game_id`)
    REFERENCES `WordCrex`.`game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`wordstate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`wordstate` (
  `state` VARCHAR(8) NOT NULL,
  PRIMARY KEY (`state`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`dictionary`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`dictionary` (
  `word` VARCHAR(15) NOT NULL,
  `letterset_code` VARCHAR(2) NOT NULL,
  `state` VARCHAR(8) NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`word`, `letterset_code`),
  INDEX `fk_woordenboek_woordstatus_idx` (`state` ASC) VISIBLE,
  INDEX `fk_woordenboek_letterset_idx` (`letterset_code` ASC) VISIBLE,
  INDEX `fk_woordenboek_account_idx` (`username` ASC) VISIBLE,
  CONSTRAINT `fk_woordenboek_account`
    FOREIGN KEY (`username`)
    REFERENCES `WordCrex`.`account` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_woordenboek_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `WordCrex`.`letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_woordenboek_woordstatus`
    FOREIGN KEY (`state`)
    REFERENCES `WordCrex`.`wordstate` (`state`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`handletter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`handletter` (
  `game_id` INT(11) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `letter_id` INT(11) NOT NULL,
  PRIMARY KEY (`game_id`, `turn_id`, `letter_id`),
  CONSTRAINT `fk_handletter_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `WordCrex`.`turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`symbol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`symbol` (
  `letterset_code` VARCHAR(2) NOT NULL,
  `symbol` CHAR(1) NOT NULL,
  `value` INT(11) NOT NULL,
  `counted` INT(11) NOT NULL,
  PRIMARY KEY (`letterset_code`, `symbol`),
  INDEX `fk_lettertype_letterset_idx` (`letterset_code` ASC) VISIBLE,
  CONSTRAINT `fk_lettertype_letterset`
    FOREIGN KEY (`letterset_code`)
    REFERENCES `WordCrex`.`letterset` (`code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`letter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`letter` (
  `letter_id` INT(11) NOT NULL,
  `game_id` INT(11) NOT NULL,
  `symbol_letterset_code` VARCHAR(2) NOT NULL,
  `symbol` CHAR(1) NOT NULL,
  PRIMARY KEY (`letter_id`, `game_id`),
  INDEX `fk_letter_symbol_idx` (`symbol_letterset_code` ASC, `symbol` ASC) VISIBLE,
  INDEX `fk_letter_gamel_idx` (`game_id` ASC) VISIBLE,
  CONSTRAINT `fk_letter_game`
    FOREIGN KEY (`game_id`)
    REFERENCES `WordCrex`.`game` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_letter_symbol`
    FOREIGN KEY (`symbol_letterset_code` , `symbol`)
    REFERENCES `WordCrex`.`symbol` (`letterset_code` , `symbol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WordCrex`.`turnboardletter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`turnboardletter` (
  `letter_id` INT(11) NOT NULL,
  `game_id` INT(11) NOT NULL,
  `turn_id` INT(11) NOT NULL,
  `tile_x` INT(11) NOT NULL,
  `tile_y` INT(11) NOT NULL,
  PRIMARY KEY (`letter_id`, `game_id`),
  UNIQUE INDEX `plaats_uniek` (`tile_x` ASC, `tile_y` ASC, `game_id` ASC) VISIBLE,
  INDEX `fk_turnboardletter_tile_idx` (`tile_x` ASC, `tile_y` ASC) VISIBLE,
  INDEX `fk_turnboardletter_turn_idx` (`game_id` ASC, `turn_id` ASC) VISIBLE,
  CONSTRAINT `fk_turnboardletter_letter`
    FOREIGN KEY (`letter_id` , `game_id`)
    REFERENCES `WordCrex`.`letter` (`letter_id` , `game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_turnboardletter_tile`
    FOREIGN KEY (`tile_x` , `tile_y`)
    REFERENCES `WordCrex`.`tile` (`x` , `y`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_turnboardletter_turn`
    FOREIGN KEY (`game_id` , `turn_id`)
    REFERENCES `WordCrex`.`turn` (`game_id` , `turn_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

USE `WordCrex` ;

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`gelegd`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`gelegd` (`game_id` INT, `turn_id` INT, `woorddeel` INT, `x-waarden` INT, `y-waarden` INT);

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`gelegdplayer1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`gelegdplayer1` (`game_id` INT, `turn_id` INT, `woorddeel` INT, `x-waarden` INT, `y-waarden` INT);

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`gelegdplayer2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`gelegdplayer2` (`game_id` INT, `turn_id` INT, `woorddeel` INT, `x-waarden` INT, `y-waarden` INT);

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`hand`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`hand` (`game_id` INT, `turn_id` INT, `inhoud` INT);

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`pot`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`pot` (`game_id` INT, `letter_id` INT, `symbol` INT);

-- -----------------------------------------------------
-- Placeholder table for view `WordCrex`.`score`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WordCrex`.`score` (`game_id` INT, `game_state` INT, `username_player1` INT, `username_player2` INT, `score1` INT, `bonus1` INT, `score2` INT, `bonus2` INT);

-- -----------------------------------------------------
-- View `WordCrex`.`gelegd`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`gelegd`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`gelegd` AS select `tbl`.`game_id` AS `game_id`,`tbl`.`turn_id` AS `turn_id`,group_concat(`l`.`symbol` order by `tbl`.`tile_x` ASC,`tbl`.`tile_y` ASC separator ',') AS `woorddeel`,group_concat(`tbl`.`tile_x` order by `tbl`.`tile_x` ASC,`tbl`.`tile_y` ASC separator ',') AS `x-waarden`,group_concat(`tbl`.`tile_y` order by `tbl`.`tile_x` ASC,`tbl`.`tile_y` ASC separator ',') AS `y-waarden` from (`WordCrex`.`turnboardletter` `tbl` join `WordCrex`.`letter` `l` on(((`l`.`game_id` = `tbl`.`game_id`) and (`l`.`letter_id` = `tbl`.`letter_id`)))) group by `tbl`.`game_id`,`tbl`.`turn_id`;

-- -----------------------------------------------------
-- View `WordCrex`.`gelegdplayer1`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`gelegdplayer1`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`gelegdplayer1` AS select `pbl`.`game_id` AS `game_id`,`pbl`.`turn_id` AS `turn_id`,group_concat(`l`.`symbol` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `woorddeel`,group_concat(`pbl`.`tile_x` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `x-waarden`,group_concat(`pbl`.`tile_y` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `y-waarden` from (`WordCrex`.`boardplayer1` `pbl` join `WordCrex`.`letter` `l` on(((`l`.`game_id` = `pbl`.`game_id`) and (`l`.`letter_id` = `pbl`.`letter_id`)))) group by `pbl`.`game_id`,`pbl`.`turn_id`;

-- -----------------------------------------------------
-- View `WordCrex`.`gelegdplayer2`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`gelegdplayer2`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`gelegdplayer2` AS select `pbl`.`game_id` AS `game_id`,`pbl`.`turn_id` AS `turn_id`,group_concat(`l`.`symbol` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `woorddeel`,group_concat(`pbl`.`tile_x` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `x-waarden`,group_concat(`pbl`.`tile_y` order by `pbl`.`tile_x` ASC,`pbl`.`tile_y` ASC separator ',') AS `y-waarden` from (`WordCrex`.`boardplayer2` `pbl` join `WordCrex`.`letter` `l` on(((`l`.`game_id` = `pbl`.`game_id`) and (`l`.`letter_id` = `pbl`.`letter_id`)))) group by `pbl`.`game_id`,`pbl`.`turn_id`;

-- -----------------------------------------------------
-- View `WordCrex`.`hand`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`hand`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`hand` AS select `hl`.`game_id` AS `game_id`,`hl`.`turn_id` AS `turn_id`,group_concat(`l`.`symbol` order by `l`.`symbol` ASC separator ',') AS `inhoud` from ((`WordCrex`.`handletter` `hl` join `WordCrex`.`letter` `l` on(((`hl`.`game_id` = `l`.`game_id`) and (`hl`.`letter_id` = `l`.`letter_id`)))) join `WordCrex`.`turn` `t` on(((`hl`.`turn_id` = `t`.`turn_id`) and (`hl`.`game_id` = `t`.`game_id`)))) group by `hl`.`game_id`,`hl`.`turn_id`;

-- -----------------------------------------------------
-- View `WordCrex`.`pot`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`pot`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`pot` AS select `l`.`game_id` AS `game_id`,`l`.`letter_id` AS `letter_id`,`l`.`symbol` AS `symbol` from `WordCrex`.`letter` `l` where ((not(`l`.`letter_id` in (select `tbl`.`letter_id` from `WordCrex`.`turnboardletter` `tbl` where (`tbl`.`game_id` = `l`.`game_id`)))) and (not(`l`.`letter_id` in (select `hl`.`letter_id` from `WordCrex`.`handletter` `hl` where ((`hl`.`game_id` = `l`.`game_id`) and `hl`.`turn_id` in (select max(`t`.`turn_id`) from `WordCrex`.`turn` `t` where (`t`.`game_id` = `l`.`game_id`))))))) order by `l`.`game_id`,`l`.`letter_id`;

-- -----------------------------------------------------
-- View `WordCrex`.`score`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WordCrex`.`score`;
USE `WordCrex`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`u17091p12601_groepd`@`localhost` SQL SECURITY DEFINER VIEW `WordCrex`.`score` AS select `g`.`game_id` AS `game_id`,`g`.`game_state` AS `game_state`,`g`.`username_player1` AS `username_player1`,`g`.`username_player2` AS `username_player2`,ifnull((select sum(`t1`.`score`) AS `totaalscore` from `WordCrex`.`turnplayer1` `t1` where (`t1`.`game_id` = `g`.`game_id`)),0) AS `score1`,ifnull((select sum(`t1`.`bonus`) AS `totaalbonus` from `WordCrex`.`turnplayer1` `t1` where (`t1`.`game_id` = `g`.`game_id`)),0) AS `bonus1`,ifnull((select sum(`t2`.`score`) AS `totaalscore` from `WordCrex`.`turnplayer2` `t2` where (`t2`.`game_id` = `g`.`game_id`)),0) AS `score2`,ifnull((select sum(`t2`.`bonus`) AS `totaalbonus` from `WordCrex`.`turnplayer2` `t2` where (`t2`.`game_id` = `g`.`game_id`)),0) AS `bonus2` from `WordCrex`.`game` `g`;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
