SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE players;
TRUNCATE TABLE games;
TRUNCATE TABLE gamemoves;
SET FOREIGN_KEY_CHECKS = 1;

--Players for GameTests

--Get:
--Get a game
INSERT INTO players (playerName, playerToken) VALUES ("Andres1", "Token1");
INSERT INTO players (playerName, playerToken) VALUES ("Andres2", "Token2");
--Not started Game // Post New Game (Same player to force new game independently of test order.
-- Already two non-started games for this player.
-- First one to force Player:4 into it (Post-create game second player)
-- Second to have it always as non-started to call from Get-non started game
-- Third to create in Post).
-- Ensure availability independently of test order of execution (cannot order Nested, only Tests)
INSERT INTO players (playerName, playerToken) VALUES ("Andres3", "Token3");

--Post:
--New Game (player id 3)
--New Game second Player
INSERT INTO players (playerName, playerToken) VALUES ("Andres4", "Token4");


--Games for GameTests:
--Get a game
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4);
--Get a game with WinningCombination
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, winner) VALUES ( 1, 2, TRUE, 2, 5, TRUE);
--Get a no started game
--First to force the Post-AddSecondPlayer into it
INSERT INTO games (player1Id) VALUES (3);
--Second to always have as NotStarted (Get non started game)
INSERT INTO games (player1Id) VALUES (3);

--GameMoves for GameTests:
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 2, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 1, 1, FALSE);

INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 0, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 1, 1, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 2, 2, 2, TRUE);