#######################################
# WORDCREX DATABASE                   #
# VULSCRIPT VOORBEELDDATA             #
# versie 1.2                          #
# VSOPRJ2 18/19                       #
# datum: 09-12-2018                   #
# (c) Ger Saris                       #
#######################################

-- -------------------------------------------------------------
-- legen in aflopende volgorde van referentiële afhankelijkheid
-- -------------------------------------------------------------

DELETE FROM boardplayer1;
DELETE FROM boardplayer2;
DELETE FROM turnboardletter;
DELETE FROM handletter;
DELETE FROM turnplayer1;
DELETE FROM turnplayer2;
DELETE FROM turn;
DELETE FROM letter;
DELETE FROM chatline;
DELETE FROM game;
DELETE FROM accountrole;
DELETE FROM account WHERE NOT `username`="bookowner";

-- -------------------------------------------------------------
-- vullen in oplopende volgorde van referentiële afhankelijkheid
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- account
-- -------------------------------------------------------------
INSERT INTO account (`username`,`password`)
VALUES
("ger","123"), ("luc", "123"), ("rik", "123"), 
("test-player","123"),
("test-observer","123"),
("test-moderator","123"),
("test-admin", "123"),
("allrights","123")
;
-- -------------------------------------------------------------
-- accountrol
-- -------------------------------------------------------------
INSERT INTO accountrole (`username`,`role`)
VALUES
("ger","player"), ("luc", "player"), ("rik", "player"),
("test-player", "player"),
("test-observer","observer"),
("test-moderator","moderator"),
("test-admin", "administrator"),
("allrights","player"),
("allrights","observer"),
("allrights","moderator"),
("allrights","administrator")
;

-- -------------------------------------------------------------
-- game
-- -------------------------------------------------------------
INSERT INTO game ( 
     `game_id`, 
     `game_state`, 
     `letterset_code`, 
     `username_player1`,
     `username_player2`,
     `answer_player2`)
VALUES
# voorbeeld spel 500, ger heeft rik uitgedaagd, er is nog geen antwoord van rik
  (500,
   "request",
   "NL",
   "ger",
   "rik",
   "unknown"
   ),
# voorbeeld spel 501, luc heeft rik utgedaagd, rik heeft de uitnodiging afgewezen
  (501,
   "request",
   "NL",
   "luc",
   "rik",
   "rejected"
   );

######################################################################

-- -------------------------------------------------------------
-- Nieuw Spel toevoegen – doen van beurten - stappen
-- -------------------------------------------------------------

# Uitdagen gaat tussen 2 spelers met ieder een account. 
# In dit voorbeeld nemen we jagermeester en Lidewij. Ze worden toegevoegd.

INSERT INTO account (`username`, `password`)
VALUES ( "jagermeester", "rrr"),("Lidewij","mmm");

# jagermeester en Lidewij moeten ook "player" zijn
INSERT INTO accountrole (`username`,`role`)
VALUES("jagermeester","player"), ("Lidewij", "player");

# jagermeester gaat Lidewij uitdagen. Daarvoor moet de App van jagermeester eerst een nieuw spel aanmaken.
# de game_id staat op auto-increment, dus die wordt niet meegegeven.

INSERT INTO game ( 
     `game_state`, `letterset_code`, `username_player1`,
     `username_player2`,`answer_player2` )
VALUES  (
      "request", "NL", "jagermeester",
      "Lidewij", "unknown");

# In dit voorbeeld-script wordt een sessievariabele gebruikt (herkenbaar aan @) 
# maar in jullie JAVAcode kun je daar een gewone variabele voor gebruiken natuurlijk.
# Gebruik een session variable voor de game_id
# even opvragen welke id het spel heeft gekregen
SET @gameid = (SELECT MAX(game_id) FROM game WHERE username_player1="jagermeester" AND username_player2="Lidewij");

# Nu is het wachten tot Lidewij de uitdaging aanneemt.
# Als Lidewij meedoet, wijzigt zijn App, het record in spel
UPDATE game SET answer_player2 = "accepted" WHERE game_id = @gameid;

# De App van jagermeester 'ziet' (door te pollen) dat de spelstatus is gewijzigd
# Nu kan het spel beginnen
# De App van jagermeester wijzigt het spelrecord
UPDATE game SET game_state = "playing" WHERE game_id = @gameid;

# De App van jagermeester maakt nu de letters aan.
# omdat de letterset_code = NL, moet dat een Nederlandse set zijn. 
# iedere letter krijgt zijn eigen (letter_)id binnen het spel
INSERT INTO letter (`letter_id`,`game_id`,`symbol_letterset_code`,`symbol`)
VALUES
(001, @gameid, "NL", "A"),
(002, @gameid, "NL", "A"),
(003, @gameid, "NL", "A"),
(004, @gameid, "NL", "A"),
(005, @gameid, "NL", "A"),
(006, @gameid, "NL", "A"),
(007, @gameid, "NL", "A"),
(008, @gameid, "NL", "B"),
(009, @gameid, "NL", "B"),
(010, @gameid, "NL", "C"),
(011, @gameid, "NL", "C"),
(012, @gameid, "NL", "D"),
(013, @gameid, "NL", "D"),
(014, @gameid, "NL", "D"),
(015, @gameid, "NL", "D"),
(016, @gameid, "NL", "D"),
(017, @gameid, "NL", "E"),
(018, @gameid, "NL", "E"),
(019, @gameid, "NL", "E"),
(020, @gameid, "NL", "E"),
(021, @gameid, "NL", "E"),
(022, @gameid, "NL", "E"),
(023, @gameid, "NL", "E"),
(024, @gameid, "NL", "E"),
(025, @gameid, "NL", "E"),
(026, @gameid, "NL", "E"),
(027, @gameid, "NL", "E"),
(028, @gameid, "NL", "E"),
(029, @gameid, "NL", "E"),
(030, @gameid, "NL", "E"),
(031, @gameid, "NL", "E"),
(032, @gameid, "NL", "E"),
(033, @gameid, "NL", "E"),
(034, @gameid, "NL", "E"),
(035, @gameid, "NL", "F"),
(036, @gameid, "NL", "F"),
(037, @gameid, "NL", "G"),
(038, @gameid, "NL", "G"),
(039, @gameid, "NL", "G"),
(040, @gameid, "NL", "H"),
(041, @gameid, "NL", "H"),
(042, @gameid, "NL", "I"),
(043, @gameid, "NL", "I"),
(044, @gameid, "NL", "I"),
(045, @gameid, "NL", "I"),
(046, @gameid, "NL", "J"),
(047, @gameid, "NL", "J"),
(048, @gameid, "NL", "K"),
(049, @gameid, "NL", "K"),
(050, @gameid, "NL", "K"),
(051, @gameid, "NL", "L"),
(052, @gameid, "NL", "L"),
(053, @gameid, "NL", "L"),
(054, @gameid, "NL", "M"),
(055, @gameid, "NL", "M"),
(056, @gameid, "NL", "M"),
(057, @gameid, "NL", "N"),
(058, @gameid, "NL", "N"),
(059, @gameid, "NL", "N"),
(060, @gameid, "NL", "N"),
(061, @gameid, "NL", "N"),
(062, @gameid, "NL", "N"),
(063, @gameid, "NL", "N"),
(064, @gameid, "NL", "N"),
(065, @gameid, "NL", "N"),
(066, @gameid, "NL", "N"),
(067, @gameid, "NL", "N"),
(068, @gameid, "NL", "O"),
(069, @gameid, "NL", "O"),
(070, @gameid, "NL", "O"),
(071, @gameid, "NL", "O"),
(072, @gameid, "NL", "O"),
(073, @gameid, "NL", "O"),
(074, @gameid, "NL", "P"),
(075, @gameid, "NL", "P"),
(076, @gameid, "NL", "Q"),
(077, @gameid, "NL", "R"),
(078, @gameid, "NL", "R"),
(079, @gameid, "NL", "R"),
(080, @gameid, "NL", "R"),
(081, @gameid, "NL", "R"),
(082, @gameid, "NL", "S"),
(083, @gameid, "NL", "S"),
(084, @gameid, "NL", "S"),
(085, @gameid, "NL", "S"),
(086, @gameid, "NL", "S"),
(087, @gameid, "NL", "T"),
(088, @gameid, "NL", "T"),
(089, @gameid, "NL", "T"),
(090, @gameid, "NL", "T"),
(091, @gameid, "NL", "T"),
(092, @gameid, "NL", "U"),
(093, @gameid, "NL", "U"),
(094, @gameid, "NL", "U"),
(095, @gameid, "NL", "V"),
(096, @gameid, "NL", "V"),
(097, @gameid, "NL", "W"),
(098, @gameid, "NL", "W"),
(099, @gameid, "NL", "X"),
(100, @gameid, "NL", "Y"),
(101, @gameid, "NL", "Z"),
(102, @gameid, "NL", "Z");

# De App van jagermeester maakt de eerste turn aan

INSERT INTO turn (`game_id`, `turn_id`)
VALUES( @gameid, 1);

# In dit voorbeeld wordt de hand gevuld met 7 random letters. 
#  In de volgende stap staan concrete waarden, maar 
#  die worden in jullie programma straks natuurlijk random bepaald
#  gebruik daarvoor de view met de naam [pot]

INSERT INTO `handletter` (`game_id`,`turn_id`,`letter_id`)
VALUES
(@gameid,1,35),(@gameid,1,8),(@gameid,1,51),(@gameid,1,10),(@gameid,1,77),(@gameid,1,17),(@gameid,1,82);

# In de view met de naam [hand] kun je nu zien wat de letters zijn
# hand bevat het veld `inhoud` dat is samengesteld uit 
# de groep letters uit de tabel [handletter] per game per turn
-- SELECT * FROM hand WHERE game_id = @gameid;

# De letters in de hand zijn binnen de inhoud gesorteerd op alfabet. 

# Je kunt nu weten welke beurt het is door te kijken naar het hoogste turn_id in de tabel [turn]
# turn_id 1 is bezig.
# Je kunt nu weten of de spelers al een beurt hebben gedaan door [turnplayer1] en [turnplayer2] te bekijken
# als in beide tabellen een turn_id met waarde = 1 voorkomt is de beurt klaar. Dat is nu nog niet.

# Lidewij legt het woord "FELS" als eerste. 
# Haar App berekent de score = 96
# de bonus wordt op 0 gelaten want er is nog geen actie van de tegenstander bekend.

INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type)
VALUES ( @gameid, 1, "Lidewij", 0, 96, "play");

INSERT INTO boardplayer2 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, "Lidewij", 1, 35, 6, 8),
(@gameid, "Lidewij", 1, 17, 7, 8),
(@gameid, "Lidewij", 1, 51, 8, 8),
(@gameid, "Lidewij", 1, 82, 9, 8);

# jagermeester legt het woord "FLES" als tweede
INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type)
VALUES ( @gameid, 1, "jagermeester", 0, 96, "play");

INSERT INTO boardplayer1 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, "jagermeester", 1, 35, 6, 8),
(@gameid, "jagermeester", 1, 51, 7, 8),
(@gameid, "jagermeester", 1, 17, 8, 8),
(@gameid, "jagermeester", 1, 82, 9, 8);

# jagermeester was de tweede die de beurtactie beeindigde, 
# dus de App van jagermeester doet nu het volgende werk:
# omdat de score gelijk is, krijgt de player die het eerste legde een bonus.

UPDATE turnplayer2 SET bonus = 5 WHERE game_id=@gameid AND turn_id=1;

# Het board wordt geupdate, de letters van de beurtwinnaar komen erin

INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 1, 35, 6, 8),
(@gameid, 1, 17, 7, 8),
(@gameid, 1, 51, 8, 8),
(@gameid, 1, 82, 9, 8);

# in de view [gelegd] kun je zien welk woord ontstaan is, oftewel wat er gelegd is in deze turn.
# Die view hoef je waarschijnlijk niet te gebruiken in je JAVAcode, 
# maar helpt wel om met de hand te checken wat de inhoud van de database is.
# zo zijn ook de views [gelegdplayer1] en [gelegdplayer2] beschikbaar

-- SELECT * FROM gelegd WHERE game_id = @gameid;
-- SELECT * FROM gelegdplayer1 WHERE game_id = @gameid;
-- SELECT * FROM gelegdplayer2 WHERE game_id = @gameid;

# nog een keer opvragen welke id het spel heeft gekregen
SET @gameid = (SELECT MAX(game_id) FROM game WHERE username_player1="jagermeester" AND username_player2="Lidewij");

# De App van jagemeester maakt een nieuwe turn
# hiet staat een hard-coded 2, maar beter is in jouw App te bepalen bij welke beurt we zijn.
SET @turnid = 2;

####################################
# ==> HIER BEGINT DE HERHALING <== #
####################################

INSERT INTO turn (`game_id`, `turn_id`)
VALUES( @gameid, @turnid);

# een nieuwe hand wordt gevuld met overgebleven letters uit de vorige hand
# aangevuld met random letters, zodat er weer 7 zijn
# de oude hand (uit de vorige beurt) blijft onaangetast

INSERT INTO `handletter` (`game_id`,`turn_id`,`letter_id`)
VALUES
(@gameid,@turnid,37),(@gameid,@turnid,8),(@gameid,@turnid,68),(@gameid,@turnid,10),(@gameid,@turnid,77),(@gameid,@turnid,46),(@gameid,@turnid,57);

# Lidewij legt het woord "JOG" als eerste. 
# Haar App berekent de score = 32
# de bonus wordt op 0 gelaten want er is nog geen actie van de tegenstander bekend.

INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type)
VALUES ( @gameid, @turnid, "Lidewij", 0, 32, "play");

INSERT INTO boardplayer2 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, "Lidewij", @turnid, 46, 5, 7),
(@gameid, "Lidewij", @turnid, 68, 6, 7),
(@gameid, "Lidewij", @turnid, 37, 7, 7);

# jagermeester legt het woord "CO" als tweede
INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type)
VALUES ( @gameid, @turnid, "jagermeester", 0, 30, "play");

INSERT INTO boardplayer1 (game_id, username, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, "jagermeester", @turnid, 10, 8, 6),
(@gameid, "jagermeester", @turnid, 68, 8, 7);

# jagermeester was de tweede die de beurtactie beeindigde, 
# dus de App van jagermeester doet nu het volgende werk:

# Het board wordt geupdate, de letters van de beurtwinnaar komen erin

INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, @turnid, 46, 5, 7),
(@gameid, @turnid, 68, 6, 7),
(@gameid, @turnid, 37, 7, 7);


####################################
# ==>GA TERUG NAAR DE HERHALING<== #
####################################

# je kunt dit blijven herhalen tot het einde van het spel.
# De volgende lijst met INSERTS maken het hele spel af tot het einde.
# Dit is niet de manier waarop je het in jouw code na moet doen.
# Het is alleen bedoeld om een volledig spel te hebben om mee te kunnen testen.


# nog een keer opvragen welke id het spel heeft gekregen
SET @gameid = (SELECT MAX(game_id) FROM game WHERE username_player1="jagermeester" AND username_player2="Lidewij");

INSERT INTO turn (`game_id`, `turn_id`)
VALUES(@gameid, 3),(@gameid, 4),(@gameid, 5),(@gameid, 6),(@gameid, 7),(@gameid, 8),(@gameid, 9),(@gameid, 10),(@gameid,11),(@gameid,12),(@gameid,13),(@gameid,14),
(@gameid,15),(@gameid,16),(@gameid,17),(@gameid,18),(@gameid,19),(@gameid,20),(@gameid,21),(@gameid,22),(@gameid,23),(@gameid,24),(@gameid,25),
(@gameid,26),(@gameid,27),(@gameid,28),(@gameid,29),(@gameid,30),(@gameid,31),(@gameid,32),(@gameid,33),(@gameid,34);

INSERT INTO `handletter` (`game_id`,`turn_id`,`letter_id`)
VALUES
(@gameid,  3, 40),(@gameid, 3,   8),(@gameid,  3, 95),(@gameid,  3, 10),(@gameid,  3, 77),(@gameid,  3, 18),(@gameid,  3, 57),
(@gameid,  4, 40),(@gameid, 4,   8),(@gameid,  4, 33),(@gameid,  4, 34),(@gameid,  4, 77),(@gameid,  4, 18),(@gameid,  4, 57),
(@gameid,  5, 36),(@gameid, 5,  78),(@gameid,  5, 87),(@gameid,  5, 11),(@gameid,  5, 28),(@gameid,  5, 76),(@gameid,  5, 49),
(@gameid,  6, 36),(@gameid, 6,  78),(@gameid,  6, 87),(@gameid,  6, 11),(@gameid,  6, 19),(@gameid,  6, 58),(@gameid,  6, 49),
(@gameid,  7, 42),(@gameid, 7,  78),(@gameid,  7, 87),(@gameid,  7, 11),(@gameid,  7, 24),(@gameid,  7, 58),(@gameid,  7, 79),
(@gameid,  8, 43),(@gameid, 8,  78),(@gameid,  8,  1),(@gameid,  8, 52),(@gameid,  8, 41),(@gameid,  8, 58),(@gameid,  8, 99),
(@gameid,  9, 59),(@gameid, 9,  78),(@gameid,  9, 41),(@gameid,  9,  1),(@gameid,  9, 20),(@gameid,  9, 21),(@gameid,  9, 58),
(@gameid, 10, 59),(@gameid, 10, 63),(@gameid, 10, 55),(@gameid, 10,  1),(@gameid, 10, 64),(@gameid, 10, 21),(@gameid, 10, 58),
(@gameid, 11, 59),(@gameid, 11, 63),(@gameid, 11, 65),(@gameid, 11, 12),(@gameid, 11, 64),(@gameid, 11, 21),(@gameid, 11, 58),
(@gameid, 12, 59),(@gameid, 12, 63),(@gameid, 12, 65),(@gameid, 12, 64),(@gameid, 12,101),(@gameid, 12,  3),(@gameid, 12, 26),
(@gameid, 13, 59),(@gameid, 13, 63),(@gameid, 13, 65),(@gameid, 13, 38),(@gameid, 13, 92),(@gameid, 13,100),(@gameid, 13, 22),
(@gameid, 14, 59),(@gameid, 14, 63),(@gameid, 14, 69),(@gameid, 14, 38),(@gameid, 14, 92),(@gameid, 14, 14),(@gameid, 14, 22),
(@gameid, 15, 59),(@gameid, 15, 63),(@gameid, 15, 69),(@gameid, 15, 39),(@gameid, 15, 60),(@gameid, 15, 15),(@gameid, 15, 70),
(@gameid, 16, 59),(@gameid, 16, 63),(@gameid, 16, 69),(@gameid, 16, 44),(@gameid, 16,  4),(@gameid, 16, 50),(@gameid, 16, 32),
(@gameid, 17, 59),(@gameid, 17, 63),(@gameid, 17, 69),(@gameid, 17,  5),(@gameid, 17,  4),(@gameid, 17, 54),(@gameid, 17, 31),
(@gameid, 18, 59),(@gameid, 18, 63),(@gameid, 18, 13),(@gameid, 18,  5),(@gameid, 18,  4),(@gameid, 18, 83),(@gameid, 18, 25),
(@gameid, 19, 59),(@gameid, 19, 63),(@gameid, 19,  6),(@gameid, 19,  5),(@gameid, 19,  4),(@gameid, 19, 84),(@gameid, 19, 25),
(@gameid, 20, 59),(@gameid, 20, 47),(@gameid, 20,  6),(@gameid, 20,  7),(@gameid, 20, 93),(@gameid, 20, 30),(@gameid, 20, 25),
(@gameid, 21, 59),(@gameid, 21, 48),(@gameid, 21,  6),(@gameid, 21,  7),(@gameid, 21, 71),(@gameid, 21, 16),(@gameid, 21, 25),
(@gameid, 22, 88),(@gameid, 22, 89),(@gameid, 22,  6),(@gameid, 22, 53),(@gameid, 22, 71),(@gameid, 22, 81),(@gameid, 22, 29),
(@gameid, 23, 62),(@gameid, 23, 89),(@gameid, 23,  2),(@gameid, 23, 67),(@gameid, 23, 71),(@gameid, 23, 72),(@gameid, 23, 96),
(@gameid, 24, 62),(@gameid, 24, 74),(@gameid, 24, 73),(@gameid, 24, 67),(@gameid, 24, 71),(@gameid, 24, 72),(@gameid, 24, 45),
(@gameid, 25, 62),(@gameid, 25, 80),(@gameid, 25, 73),(@gameid, 25, 67),(@gameid, 25, 85),(@gameid, 25, 72),(@gameid, 25, 45),
(@gameid, 26, 62),(@gameid, 26, 75),(@gameid, 26,  9),(@gameid, 26, 67),(@gameid, 26, 97),(@gameid, 26,102),(@gameid, 26, 45),
(@gameid, 27, 62),(@gameid, 27, 75),(@gameid, 27,  9),(@gameid, 27, 67),(@gameid, 27, 56),(@gameid, 27,102),(@gameid, 27, 27),
(@gameid, 28, 62),(@gameid, 28, 86),(@gameid, 28,  9),(@gameid, 28, 67),(@gameid, 28, 56),(@gameid, 28, 90),(@gameid, 28, 66),
(@gameid, 29, 62),(@gameid, 29, 61),(@gameid, 29,  9),(@gameid, 29, 67),(@gameid, 29, 56),(@gameid, 29, 23),(@gameid, 29, 66),
(@gameid, 30, 62),(@gameid, 30, 61),(@gameid, 30,  9),(@gameid, 30, 67),(@gameid, 30, 56),(@gameid, 30, 90),(@gameid, 30, 98),
(@gameid, 31, 62),(@gameid, 31, 61),(@gameid, 31, 94),(@gameid, 31, 67),(@gameid, 31, 56),
(@gameid, 32, 62),(@gameid, 32, 61),(@gameid, 32, 56),
(@gameid, 33, 62),(@gameid, 33, 61),
(@gameid, 34, 62);

INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type)
VALUES 
( @gameid, 3, "jagermeester", 0, 66, "play"),
( @gameid, 4, "jagermeester", 0,138, "play"),
( @gameid, 5, "jagermeester", 5,248, "play"),
( @gameid, 6, "jagermeester", 0, 72, "play"),
( @gameid, 7, "jagermeester", 0, 32, "play"),
( @gameid, 8, "jagermeester", 0, 60, "play"),
( @gameid, 9, "jagermeester", 0, 73, "play"),
( @gameid,10, "jagermeester", 5, 51, "play"),
( @gameid,11, "jagermeester", 0, 21, "play"),
( @gameid,12, "jagermeester", 0, 49, "play"),
( @gameid,13, "jagermeester", 0, 48, "play"),
( @gameid,14, "jagermeester", 0, 80, "play"),
( @gameid,15, "jagermeester", 0, 32, "play"),
( @gameid,16, "jagermeester", 0, 65, "play"),
( @gameid,17, "jagermeester", 0, 27, "play"),
( @gameid,18, "jagermeester", 0, 28, "play"),
( @gameid,19, "jagermeester", 0, 27, "play"),
( @gameid,20, "jagermeester", 0, 37, "play"),
( @gameid,21, "jagermeester", 0, 38, "play"),
( @gameid,22, "jagermeester", 0, 28, "play"),
( @gameid,23, "jagermeester", 0, 19, "play"),
( @gameid,24, "jagermeester", 0, 29, "play"),
( @gameid,25, "jagermeester", 0, 23, "play"),
( @gameid,26, "jagermeester", 0, 26, "play"),
( @gameid,27, "jagermeester", 0, 33, "play"),
( @gameid,28, "jagermeester", 0, 25, "play"),
( @gameid,29, "jagermeester", 5, 25, "play"),
( @gameid,30, "jagermeester", 0, 11, "play"),
( @gameid,31, "jagermeester", 0,  9, "play"),
( @gameid,32, "jagermeester", 0,  6, "play"),
( @gameid,33, "jagermeester", 0,  6, "play"),
( @gameid,34, "jagermeester", 5,  6, "play");


INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type)
VALUES 
( @gameid, 3, "Lidewij", 0, 71, "play"),
( @gameid, 4, "Lidewij", 5,138, "play"),
( @gameid, 5, "Lidewij", 0,248, "play"),
( @gameid, 6, "Lidewij", 0,105, "play"),
( @gameid, 7, "Lidewij", 0, 34, "play"),
( @gameid, 8, "Lidewij", 0, 55, "play"),
( @gameid, 9, "Lidewij", 5, 73, "play"),
( @gameid,10, "Lidewij", 0, 51, "play"),
( @gameid,11, "Lidewij", 5, 21, "play"),
( @gameid,12, "Lidewij", 0, 44, "play"),	
( @gameid,13, "Lidewij", 0, 80, "play"),
( @gameid,14, "Lidewij", 0, 83, "play"),
( @gameid,15, "Lidewij", 0, 45, "play"),
( @gameid,16, "Lidewij", 0, 69, "play"),
( @gameid,17, "Lidewij", 0, 35, "play"),
( @gameid,18, "Lidewij", 0, 51, "play"),
( @gameid,19, "Lidewij", 0, 36, "play"),
( @gameid,20, "Lidewij", 0, 57, "play"),
( @gameid,21, "Lidewij", 0, 73, "play"),
( @gameid,22, "Lidewij", 0,102, "play"),
( @gameid,23, "Lidewij", 0, 44, "play"),
( @gameid,24, "Lidewij", 0, 34, "play"),
( @gameid,25, "Lidewij", 0, 28, "play"),
( @gameid,26, "Lidewij", 0, 27, "play"),
( @gameid,27, "Lidewij", 0, 27, "play"),
( @gameid,28, "Lidewij", 0, 27, "play"),
( @gameid,29, "Lidewij", 0, 25, "play"),
( @gameid,30, "Lidewij", 0, 19, "play"),
( @gameid,31, "Lidewij", 0, 19, "play"),
( @gameid,32, "Lidewij", 0,  9, "play"),
( @gameid,33, "Lidewij", 0,  8, "play"),
( @gameid,34, "Lidewij", 0,  6, "play");

# alle woorden (letters van woorden) op het speelbord #

# turn 3 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 3, 10, 4, 6),
(@gameid, 3, 95, 5, 6);

# turn 4 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 4,  8, 10, 7),
(@gameid, 4, 33, 10, 8),
(@gameid, 4, 40, 10, 9),
(@gameid, 4, 34, 10,10),
(@gameid, 4, 77, 10,11),
(@gameid, 4, 18, 10,12),
(@gameid, 4, 57, 10,13);

# turn 5 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 5, 28, 11, 9),
(@gameid, 5, 76, 11,10);

# turn 6 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 6, 49, 2,5),
(@gameid, 6, 19, 3,5),
(@gameid, 6, 36, 4,5);

# turn 7 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 7, 11, 6, 12),
(@gameid, 7, 42, 7, 12),
(@gameid, 7, 87, 8, 12),
(@gameid, 7, 24, 9, 12),
(@gameid, 7, 79,11, 12);

# turn 8 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 8, 52,12,9),
(@gameid, 8, 43,13,9),
(@gameid, 8, 99,14,9);

# turn 9 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 9, 41,13,8),
(@gameid, 9, 20,14,8),
(@gameid, 9, 78,15,8);

# turn 10 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 10, 55, 14, 7),
(@gameid, 10,  1, 15, 7);

# turn 11 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid,11, 21, 8, 14),
(@gameid,11, 58, 9, 14),
(@gameid,11, 12,10, 14);

# turn 12 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 12,  3, 2,6),
(@gameid, 12,101, 2,7),
(@gameid, 12, 26, 2,8),
(@gameid, 12, 64, 2,9);

# turn 13 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 13, 65, 1, 8),
(@gameid, 13,100, 3, 8);

# turn 14 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 14, 14, 5,15),
(@gameid, 14, 22, 6,15),
(@gameid, 14, 92, 7,15),
(@gameid, 14, 38, 8,15);

# turn 15 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 15, 15, 2,10),
(@gameid, 15, 70, 3,10),
(@gameid, 15, 60, 4,10),
(@gameid, 15, 39, 5,10);

# turn 16 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 16, 32, 2,11),
(@gameid, 16, 44, 3,11),
(@gameid, 16, 50, 4,11);

# turn 17 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 17, 31,10,15),
(@gameid, 17, 54,11,15),
(@gameid, 17, 69,12,15);

# turn 18 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 18, 83,12,10),
(@gameid, 18, 13,12,11);

# turn 19 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 19,  4,15, 3),
(@gameid, 19, 84,15, 4),
(@gameid, 19, 63,15, 5),
(@gameid, 19,  5,15, 6);

# turn 20 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 20, 47,12, 5),
(@gameid, 20, 30,13, 5),
(@gameid, 20, 93,14, 5);

# turn 21 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 22,  7,11,13),
(@gameid, 22, 48,12,13),
(@gameid, 22, 25,13,13),
(@gameid, 22, 59,14,13),
(@gameid, 22, 16,15,13);

# turn 22 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 22, 81, 1,11),
(@gameid, 22,  6, 1,12),
(@gameid, 22, 88, 1,13),
(@gameid, 22, 29, 1,14),
(@gameid, 22, 53, 1,15);

# turn 23 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 23, 96,11, 6),
(@gameid, 23,  2,12, 6),
(@gameid, 23, 89,13, 6);

# turn 24 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 24, 71,15,14),
(@gameid, 24, 74,15,15);

# turn 25 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 25, 80, 4,13),
(@gameid, 25, 72, 5,13),
(@gameid, 25, 73, 6,13),
(@gameid, 25, 85, 7,13);

# turn 26 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 26, 97,14,11),
(@gameid, 26, 45,14,12);

# turn 27 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 27, 75,13,12),
(@gameid, 27,102,13,14),
(@gameid, 27, 27,13,15);

# turn 28 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 28, 86, 6, 6),
(@gameid, 28, 90, 6, 9);

# turn 29 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 29, 23, 6, 10),
(@gameid, 29, 66, 7, 10);

# turn 30 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 30,  9,14, 1),
(@gameid, 30, 91,14, 2),
(@gameid, 30, 98,14, 3);

# turn 31 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 31, 67,13, 1),
(@gameid, 31, 94,13, 2);

# turn 32 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 32, 56, 9, 11);

# turn 33 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 33, 61, 12, 7);

# turn 34 #
INSERT INTO turnboardletter (game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
(@gameid, 34, 62, 12, 2);

###################################
# alle woorden van speler Lidewij # 
###################################

# turn 3 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 3, 10, 4, 6),
("Lidewij", @gameid, 3, 95, 5, 6);

# turn 4 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 4,  8, 10, 7),
("Lidewij", @gameid, 4, 33, 10, 8),
("Lidewij", @gameid, 4, 40, 10, 9),
("Lidewij", @gameid, 4, 34, 10,10),
("Lidewij", @gameid, 4, 77, 10,11),
("Lidewij", @gameid, 4, 18, 10,12),
("Lidewij", @gameid, 4, 57, 10,13);

# turn 5 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 5, 28, 11, 9),
("Lidewij", @gameid, 5, 76, 11,10);

# turn 6 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 6, 49, 2,5),
("Lidewij", @gameid, 6, 19, 3,5),
("Lidewij", @gameid, 6, 36, 4,5);

# turn 7 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 7, 11, 6, 12),
("Lidewij", @gameid, 7, 42, 7, 12),
("Lidewij", @gameid, 7, 87, 8, 12),
("Lidewij", @gameid, 7, 24, 9, 12),
("Lidewij", @gameid, 7, 79,11, 12);

# turn 8 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 8, 99,7,11),
("Lidewij", @gameid, 8, 43,8,11);

# turn 9 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 9, 41,13,8),
("Lidewij", @gameid, 9, 20,14,8),
("Lidewij", @gameid, 9, 78,15,8);

# turn 10 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 10, 55, 14, 7),
("Lidewij", @gameid, 10,  1, 15, 7);

# turn 11 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 11, 21, 8, 14),
("Lidewij", @gameid, 11, 58, 9, 14),
("Lidewij", @gameid, 11, 12,10, 14);

# turn 12 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 12,  3, 5,15),
("Lidewij", @gameid, 12,101, 6,15),
("Lidewij", @gameid, 12, 26, 7,15),
("Lidewij", @gameid, 12, 64, 8,15);

# turn 13 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 13, 65, 1, 8),
("Lidewij", @gameid, 13,100, 3, 8);

# turn 14 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 14, 14, 5,15),
("Lidewij", @gameid, 14, 22, 6,15),
("Lidewij", @gameid, 14, 92, 7,15),
("Lidewij", @gameid, 14, 38, 8,15);

# turn 15 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 15, 15, 2,10),
("Lidewij", @gameid, 15, 70, 3,10),
("Lidewij", @gameid, 15, 60, 4,10),
("Lidewij", @gameid, 15, 39, 5,10);

# turn 16 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 16, 32, 2,11),
("Lidewij", @gameid, 16, 44, 3,11),
("Lidewij", @gameid, 16, 50, 4,11);

# turn 17 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 17, 31,10,15),
("Lidewij", @gameid, 17, 54,11,15),
("Lidewij", @gameid, 17, 69,12,15);

# turn 18 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 18, 83,12,10),
("Lidewij", @gameid, 18, 13,12,11);

# turn 19 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 19,  4,15, 3),
("Lidewij", @gameid, 19, 84,15, 4),
("Lidewij", @gameid, 19, 63,15, 5),
("Lidewij", @gameid, 19,  5,15, 6);

# turn 20 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 20, 47,12, 5),
("Lidewij", @gameid, 20, 30,13, 5),
("Lidewij", @gameid, 20, 93,14, 5);

# turn 21 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 21,  7,11,13),
("Lidewij", @gameid, 21, 48,12,13),
("Lidewij", @gameid, 21, 25,13,13),
("Lidewij", @gameid, 21, 59,14,13),
("Lidewij", @gameid, 21, 16,15,13);

# turn 22 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 22, 81, 1,11),
("Lidewij", @gameid, 22,  6, 1,12),
("Lidewij", @gameid, 22, 88, 1,13),
("Lidewij", @gameid, 22, 29, 1,14),
("Lidewij", @gameid, 22, 53, 1,15);

# turn 23 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 23, 96,11, 6),
("Lidewij", @gameid, 23,  2,12, 6),
("Lidewij", @gameid, 23, 89,13, 6);

# turn 24 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 24, 71,15,14),
("Lidewij", @gameid, 24, 74,15,15);

# turn 25 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 25, 80, 4,13),
("Lidewij", @gameid, 25, 72, 5,13),
("Lidewij", @gameid, 25, 73, 6,13),
("Lidewij", @gameid, 25, 85, 7,13);

# turn 26 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 26, 97,14,11),
("Lidewij", @gameid, 26, 45,14,12);

# turn 27 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 27,102,8,10),
("Lidewij", @gameid, 27, 27,8,11);

# turn 28 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 28, 86, 6, 6),
("Lidewij", @gameid, 28, 90, 6, 9);

# turn 29 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 29, 23, 6, 10),
("Lidewij", @gameid, 29, 66, 7, 10);

# turn 30 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 30,  9,14, 1),
("Lidewij", @gameid, 30, 91,14, 2),
("Lidewij", @gameid, 30, 98,14, 3);

# turn 31 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 31, 67,13, 1),
("Lidewij", @gameid, 31, 94,13, 2);

# turn 32 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 32, 56, 9, 11);

# turn 33 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 33, 61, 12, 7);

# turn 34 #
INSERT INTO boardplayer2 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("Lidewij", @gameid, 34, 62, 12, 2);


########################################
# alle woorden van speler jagermeester # 
########################################

# turn 3 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 3, 57, 4, 6),
("jagermeester", @gameid, 3, 95, 5, 6);

# turn 4 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 4,  8, 10, 7),
("jagermeester", @gameid, 4, 33, 10, 8),
("jagermeester", @gameid, 4, 40, 10, 9),
("jagermeester", @gameid, 4, 34, 10,10),
("jagermeester", @gameid, 4, 77, 10,11),
("jagermeester", @gameid, 4, 18, 10,12),
("jagermeester", @gameid, 4, 57, 10,13);

# turn 5 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 5, 28, 11, 9),
("jagermeester", @gameid, 5, 76, 11,10);

# turn 6 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 6, 36, 3,5),
("jagermeester", @gameid, 6, 11, 4,5);

# turn 7 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 7, 11, 6, 12),
("jagermeester", @gameid, 7, 24, 7, 12),
("jagermeester", @gameid, 7, 58, 8, 12),
("jagermeester", @gameid, 7, 87, 9, 12),
("jagermeester", @gameid, 7, 79,11, 12);

# turn 8 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 8, 53,12, 9),
("jagermeester", @gameid, 8, 43,13, 9),
("jagermeester", @gameid, 8, 99,14, 9);

# turn 9 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 9, 41,13,8),
("jagermeester", @gameid, 9, 20,14,8),
("jagermeester", @gameid, 9, 78,15,8);

# turn 10 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 10, 55, 14, 7),
("jagermeester", @gameid, 10,  1, 15, 7);

# turn 11 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 11, 21, 8, 14),
("jagermeester", @gameid, 11, 58, 9, 14),
("jagermeester", @gameid, 11, 12,10, 14);

# turn 12 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 12,  3, 2, 6),
("jagermeester", @gameid, 12,101, 2, 7),
("jagermeester", @gameid, 12, 26, 2, 8),
("jagermeester", @gameid, 12, 64, 2, 9);

# turn 13 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 13,100, 1, 8),
("jagermeester", @gameid, 13, 65, 3, 8);

# turn 14 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 14, 14, 5,15),
("jagermeester", @gameid, 14, 69, 6,15),
("jagermeester", @gameid, 14, 22, 7,15),
("jagermeester", @gameid, 14, 38, 8,15);

# turn 15 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 15, 39, 4,11),
("jagermeester", @gameid, 15, 70, 5,11),
("jagermeester", @gameid, 15, 15, 6,11);

# turn 16 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 16, 32, 2,11),
("jagermeester", @gameid, 16, 63, 3,11),
("jagermeester", @gameid, 16, 50, 4,11);

# turn 17 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 17, 69,11, 5),
("jagermeester", @gameid, 17, 54,11, 6),
("jagermeester", @gameid, 17,  4,11, 7);

# turn 18 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 18, 13,11, 3),
("jagermeester", @gameid, 18,  5,11, 4),
("jagermeester", @gameid, 18, 63,11, 5),
("jagermeester", @gameid, 18, 83,11, 6),
("jagermeester", @gameid, 18, 25,11, 7);

# turn 19 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 19, 84, 6, 6),
("jagermeester", @gameid, 19,  4, 6, 9);

# turn 20 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 20, 47,11, 6),
("jagermeester", @gameid, 20,  7,11, 7);

# turn 21 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 21, 48, 3,13),
("jagermeester", @gameid, 21,  6, 4,13),
("jagermeester", @gameid, 21, 59, 5,13),
("jagermeester", @gameid, 21, 71, 6,13),
("jagermeester", @gameid, 21, 16, 7,13),
("jagermeester", @gameid, 21, 25, 8,13);

# turn 22 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 22, 88, 3,13),
("jagermeester", @gameid, 22,  6, 4,13),
("jagermeester", @gameid, 22, 81, 5,13),
("jagermeester", @gameid, 22, 71, 6,13),
("jagermeester", @gameid, 22, 89, 7,13);

# turn 23 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 23, 71, 7, 1),
("jagermeester", @gameid, 23, 67, 7, 2),
("jagermeester", @gameid, 23, 89, 7, 3),
("jagermeester", @gameid, 23, 96, 7, 4),
("jagermeester", @gameid, 23,  2, 7, 5),
("jagermeester", @gameid, 23, 62, 7, 6);

# turn 24 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 24, 71, 7, 6),
("jagermeester", @gameid, 24, 74, 8, 6);

# turn 25 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 25, 85, 6,10),
("jagermeester", @gameid, 25, 45, 6,11);

# turn 26 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 26, 45, 8, 9),
("jagermeester", @gameid, 26, 75, 8,10);

# turn 27 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 27, 75,13,12),
("jagermeester", @gameid, 27,102,13,14),
("jagermeester", @gameid, 27, 27,13,15);

# turn 28 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 28, 86, 6, 6);

# turn 29 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 29, 23, 6, 10),
("jagermeester", @gameid, 29, 66, 7, 10);

# turn 30 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 30, 91, 6, 14);

# turn 31 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 31, 67,12, 2),
("jagermeester", @gameid, 31, 94,13, 2);

# turn 32 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 32, 62, 12, 2);

# turn 33 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 33, 62, 12, 2);

# turn 34 #
INSERT INTO boardplayer1 (username, game_id, turn_id, letter_id, tile_x, tile_y)
VALUES
("jagermeester", @gameid, 34, 62, 12, 2);

################################
-- administratie achteraf

UPDATE game SET game_state="finished", username_winner="Lidewij" WHERE game_id=@gameid;

