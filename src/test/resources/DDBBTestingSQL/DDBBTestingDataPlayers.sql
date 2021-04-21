SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE players;
TRUNCATE TABLE games;
TRUNCATE TABLE gamemoves;
SET FOREIGN_KEY_CHECKS = 1;

--Get Player with Games
INSERT INTO players (playerName, playerToken) VALUES ("Andres1", "Token1");
--Get Player with no Games
INSERT INTO players (playerName, playerToken) VALUES ("Andres2", "Token2");
--Get Player with Game not started
INSERT INTO players (playerName, playerToken) VALUES ("Andres3", "Token3");
--Update Player
INSERT INTO players (playerName, playerToken) VALUES ("Andres4", "Token4");
--Delete Player with Games
INSERT INTO players (playerName, playerToken) VALUES ("Andres5", "Token5");
--Delete player with no Games
INSERT INTO players (playerName, playerToken) VALUES ("Andres6", "Token6");
--Get Deleted Player
INSERT INTO players (playerName, playerToken, active) VALUES ("Andres7", "Token7", FALSE );


--Games for PlayerTests:
--Game for Players Tests (Delete Player with games)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 5, TRUE, 5, 0);
--Game for Players Tests (Delete Player with games)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 5, TRUE, 5, 0);

------------------------------------------------------------

--Players for GameTests:
--Delete Player for GameTest1
INSERT INTO players (playerName, playerToken) VALUES ("Andres8", "Token8");
--Delete player for GameTest2
INSERT INTO players (playerName, playerToken) VALUES ("Andres9", "Token9");
--For GameTest (newGame)
INSERT INTO players (playerName, playerToken) VALUES ("Andres10", "Token10");
-- For GameTest (addSecondPlayer)
INSERT INTO players (playerName, playerToken) VALUES ("Andres11", "Token11");

--Get Game
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 2);
--Add player (addSecondPlayer)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 7, NULL, FALSE, 0, 0);
--makeMove
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--makeMove not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--makeMove out of boundaries
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--make Winning move
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 4);
--make draw move
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 8);
--Surrender on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--Surrender not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--Move on Winner
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, winner) VALUES ( 1, 4, TRUE, 4, 5, TRUE);
--Move on draw
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, draw) VALUES ( 1, 4, TRUE, 4, 9, TRUE);
--Move on surrendered
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, surrendered) VALUES ( 1, 4, TRUE, 4, 0, TRUE);
--Deleted Player on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);
--Deleted Player not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 0);





-- Some Moves in a game
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 1, 1, 1, FALSE);

-- WinningMove
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 2, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 1, 2, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 1, 1, FALSE);
-- INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 2, 2, 2, FALSE); --WinningMove

-- Draw Move
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 2, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 1, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 0, 1, FALSE);
-- INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 2, 4, FALSE); -- draw move

-- Winning
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 2, 4, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 1, 2, 4, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 2, 2, 4, TRUE);

--Draw
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 2, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 1, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 2, 4, FALSE);