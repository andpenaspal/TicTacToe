SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE players;
TRUNCATE TABLE games;
TRUNCATE TABLE gamemoves;
SET FOREIGN_KEY_CHECKS = 1;

--Get Player
INSERT INTO players (playerName, playerToken) VALUES ("Andres1", "Token1");
--Update Player
INSERT INTO players (playerName, playerToken) VALUES ("Andres2", "Token2"); 
--Delete Player
INSERT INTO players (playerName, playerToken) VALUES ("Andres3", "Token3"); 
--Delete Player for GameTest1
INSERT INTO players (playerName, playerToken) VALUES ("Andres4", "Token4"); 
--Delete player from GameTest2
INSERT INTO players (playerName, playerToken) VALUES ("Andres5", "Token5"); 
--For GameTest (newGame)
INSERT INTO players (playerName, playerToken) VALUES ("Andres6", "Token6"); 
-- For GameTest (addSecondPlayer)
INSERT INTO players (playerName, playerToken) VALUES ("Andres7", "Token7"); 

--Get Game
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 2); 
--Add player (addSecondPlayer)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 6, NULL, FALSE, 0, 0); 
--makeMove
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--makeMove not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--makeMove out of boundaries
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--make Winning move
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4); 
--make draw move
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 8); 
--Surrender on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--Surrender not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--Move on Winner
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, winner) VALUES ( 1, 2, TRUE, 2, 5, TRUE); 
--Move on draw
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, draw) VALUES ( 1, 2, TRUE, 2, 9, TRUE); 
--Move on surrendered
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, surrendered) VALUES ( 1, 2, TRUE, 2, 0, TRUE); 
--Deleted Player on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 
--Deleted Player not on turn
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 0); 

INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 1, 1, 1, FALSE);

-- WinningMove
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 1, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 1, 1, FALSE);
-- INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 2, 2, 2, FALSE); --WinningMove

-- Draw Move
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 1, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 0, 1, FALSE);
-- INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 2, 2, FALSE); -- draw move

-- Winning
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 2, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 1, 2, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 2, 2, 2, TRUE);

--Draw
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 1, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 2, 2, FALSE);