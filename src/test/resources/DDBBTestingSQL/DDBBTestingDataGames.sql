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
--Aso: player for no authorization in games of players 1 and 2
INSERT INTO players (playerName, playerToken) VALUES ("Andres3", "Token3");

--Post:
--New Game (player id 3)
--New Game second Player (add to new game)
INSERT INTO players (playerName, playerToken) VALUES ("Andres4", "Token4");

--Delete:
INSERT INTO players (playerName, playerToken) VALUES ("Andres5", "Token5");
INSERT INTO players (playerName, playerToken) VALUES ("Andres6", "Token6");




--Games for GameTests:
--Get a game
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4);
--Get a game with WinningCombination (Patch too)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, winner) VALUES ( 1, 2, TRUE, 2, 5, TRUE);
--Get a no started game
--First to force the Post-AddSecondPlayer into it
INSERT INTO games (player1Id) VALUES (3);
--Second to always have as NotStarted (Get, Patch non started game)
INSERT INTO games (player1Id) VALUES (3);

--Post(none) (gameId 14)

--Patch (gameId: 4 till here)
--MakeMove (5)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4);
--DrawMove (6)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 8);
--WinningMove (7)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4);
--On Draw (8)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, draw) VALUES ( 1, 2, TRUE, 1, 9, TRUE);
--On Win (Game 2 (Get Win))
--On Surrendered (9)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter, surrendered) VALUES ( 1, 2, TRUE, 1, 5, TRUE);
--Incorrect Turn (10)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 1, 2, TRUE, 2, 4);

--Delete
--On Turn (11)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 5, 6, TRUE, 6, 4);
--Incorrect Turn (12)
INSERT INTO games (player1Id, player2Id, gameStarted, turn, turnCounter) VALUES ( 5, 6, TRUE, 6, 4);
--No Started Game (13)
INSERT INTO games (player1Id) VALUES (5);
--No Authenticated (no Game needed)
--No Authorized(Use some game from Player1&2)
--Surrendered/Draw/Won (the one on Patch & Players too)


--GameMoves for GameTests:
--Some Moves
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 2, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (1, 0, 1, 1, FALSE);
--Won
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 0, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 1, 1, 2, TRUE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 0, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (2, 2, 2, 2, TRUE);

INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (5, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (5, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (5, 2, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (5, 0, 1, 1, FALSE);
--ToDraw
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 1, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 2, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 1, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 2, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 1, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 0, 2, 1, FALSE);
--INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (6, 2, 2, 2, FALSE);
--ToWin
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 1, 1, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 0, 2, 1, FALSE);
--INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (7, 2, 2, 2, TRUE);
--Draw
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 1, 0, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 2, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 0, 1, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 2, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 1, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 0, 2, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (8, 2, 2, 2, FALSE);
--On surrender (some moves)
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (9, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (9, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (9, 2, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (9, 0, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (9, 0, 2, 2, FALSE);
--Incorrect turn (some moves)
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 0, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 1, 1, 1, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 2, 2, 2, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (10, 0, 1, 1, FALSE);

--Some Moves (Delete on turn)
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 0, 6, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 1, 1, 5, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 2, 2, 6, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (11, 0, 1, 5, FALSE);

--Some Moves (Delete Incorrect Turn)
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (12, 0, 0, 6, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (12, 1, 1, 5, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (12, 2, 2, 6, FALSE);
INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId, winningMove) VALUES (12, 0, 1, 5, FALSE);