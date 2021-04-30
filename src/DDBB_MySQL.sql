create table players
(
    playerId    int auto_increment
        primary key,
    playerName  varchar(255)         not null,
    playerToken varchar(255)         not null,
    active      tinyint(1) default 1 not null
);

create table games
(
    gameId      int auto_increment
        primary key,
    player1Id   int                  not null,
    player2Id   int                  null,
    gameStarted tinyint(1) default 0 not null,
    turn        tinyint    default 0 not null,
    turnCounter tinyint    default 0 not null,
    winner      tinyint(1) default 0 not null,
    draw        tinyint(1) default 0 not null,
    surrendered tinyint(1) default 0 not null,
    constraint games___fk1
        foreign key (player1Id) references players (playerId),
    constraint games___fk2
        foreign key (player2Id) references players (playerId)
);

create table gamemoves
(
    gameId      int                  not null,
    moveColumn  tinyint              not null,
    moveRow     tinyint              not null,
    playerId    int                  not null,
    winningMove tinyint(1) default 0 not null,
    primary key (gameId, moveColumn, moveRow),
    constraint gamemoves_games_gameId_fk
        foreign key (gameId) references games (gameId),
    constraint gamemoves_players_playerId_fk
        foreign key (playerId) references players (playerId),
    constraint moveColumn
        check (`moveColumn` < 3),
    constraint moveRow
        check (`moveRow` < 3)
);