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
--Delete player with no started Game
INSERT INTO players (playerName, playerToken) VALUES ("Andres7", "Token7");
--Delete player Token Boundaries
INSERT INTO players (playerName, playerToken) VALUES ("Andres8", "Token8");
--Get Deleted Player
INSERT INTO players (playerName, playerToken, active) VALUES ("Andres9", "Token9", FALSE );


--Games for PlayerTests:

--Game for Player1 (Get player with Games) and 4 (not relevant)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 4, TRUE, 4, 2);
--Game for Player3 (Get player with no started Game)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 3, NULL, FALSE, 0, 0);
--Game for Delete Player with games
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 5, TRUE, 5, 0);
--Game for Delete player with no Started Game
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 7, NULL, FALSE, 0, 0);

-- Some Moves in a game
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 0, 4, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 1, 1, 1, FALSE);

