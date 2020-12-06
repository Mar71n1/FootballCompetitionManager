--Skrypt do utworzenia bazy danych
DROP TABLE IF EXISTS Matches;
DROP TABLE IF EXISTS CompetitionTeams;
DROP TABLE IF EXISTS LeagueSeasons;
DROP TABLE IF EXISTS Competitions;
DROP TABLE IF EXISTS TeamUsers;
DROP TABLE IF EXISTS Teams;
DROP TABLE IF EXISTS Reports;
DROP TABLE IF EXISTS UserRoles;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS CompetitionStatuses;
DROP TABLE IF EXISTS MatchStatuses;
DROP TABLE IF EXISTS RequestStatuses;
DROP TABLE IF EXISTS Roles;

CREATE TABLE Roles (
	RoleId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL
);

CREATE TABLE RequestStatuses (
	RequestStatusId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL
);

CREATE TABLE MatchStatuses (
	MatchStatusId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL
);

CREATE TABLE CompetitionStatuses (
	CompetitionStatusId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL
);

CREATE TABLE Users (
	UserId int PRIMARY KEY IDENTITY(1, 1),
	Email nvarchar(100) UNIQUE NOT NULL,
	Username nvarchar(50) UNIQUE NOT NULL,
	[Password] nvarchar(16) NOT NULL,
	CreationDate date NOT NULL DEFAULT GETDATE(),
);

CREATE TABLE UserRoles (
	UserId int NOT NULL,
	RoleId int NOT NULL DEFAULT 1,
	CONSTRAINT PK_UserRoles PRIMARY KEY (UserId, RoleId),
	CONSTRAINT FK_UserRoles_Users FOREIGN KEY (UserId) REFERENCES Users(UserId),
	CONSTRAINT FK_UserRoles_Roles FOREIGN KEY (RoleId) REFERENCES Roles(RoleId)
);

CREATE TABLE Reports (
	ReportId int PRIMARY KEY IDENTITY(1, 1),
	[Description] nvarchar(255) NOT NULL,
	IsSolved bit NOT NULL DEFAULT 0,
	UserId int NOT NULL,
	SentDate date NOT NULL DEFAULT GETDATE(),
	SolvedDate date NULL
	CONSTRAINT FK_Reports_Users FOREIGN KEY (UserId) References Users(UserId)
);

CREATE TABLE Teams (
	TeamId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL,
	OwnerId int NOT NULL,
	[Description] nvarchar(255),
	CreationDate date NOT NULL DEFAULT GETDATE(),
	CONSTRAINT FK_Teams_Users FOREIGN KEY (OwnerId) REFERENCES Users(UserId)
);

CREATE TABLE TeamUsers (
	TeamId int NOT NULL,
	UserId int NOT NULL,
	RequestStatusId int NOT NULL DEFAULT 1,
	CONSTRAINT PK_TeamUsers PRIMARY KEY (TeamId, UserId),
	CONSTRAINT FK_TeamUsers_Teams FOREIGN KEY (TeamId) REFERENCES Teams(TeamId),
	CONSTRAINT FK_TeamUsers_Users FOREIGN KEY (UserId) REFERENCES Users(UserId),
	CONSTRAINT FK_TeamUsers_RequestStatuses FOREIGN KEY (RequestStatusId) REFERENCES RequestStatuses(RequestStatusId)
);

CREATE TABLE Competitions (
	CompetitionId int PRIMARY KEY IDENTITY(1, 1),
	[Name] nvarchar(50) UNIQUE NOT NULL,
	NumberOfTeams int NOT NULL,
	MatchLength int NOT NULL,
	PlayersPerTeam int NOT NULL,
	[OwnerId] int NOT NULL,
	[Description] nvarchar(255),
	CreationDate date NOT NULL DEFAULT GETDATE(),
	CompetitionStatusId int NOT NULL DEFAULT 1,
	CONSTRAINT FK_Competitions_Users FOREIGN KEY (OwnerId) REFERENCES Users(UserId),
	CONSTRAINT FK_Competitions_CompetitionStatuses FOREIGN KEY (CompetitionStatusId) REFERENCES CompetitionStatuses(CompetitionStatusId)
);

CREATE TABLE LeagueSeasons (
	LeagueSeasonId int PRIMARY KEY IDENTITY(1, 1),
	CompetitionId int NOT NULL,
	DoubleMatches bit NOT NULL DEFAULT 1,
	CONSTRAINT FK_LeagueSeasons_Competitions FOREIGN KEY (CompetitionId) REFERENCES Competitions(CompetitionId)
);

CREATE TABLE CompetitionTeams (
	CompetitionId int NOT NULL,
	TeamId int NOT NULL,
	RequestStatusId int NOT NULL DEFAULT 1,
	CONSTRAINT PK_CompetitionTeams PRIMARY KEY (CompetitionId, TeamId),
	CONSTRAINT FK_CompetitionTeams_Competitions FOREIGN KEY (CompetitionId) REFERENCES Competitions(CompetitionId),
	CONSTRAINT FK_CompetitionTeams_Teams FOREIGN KEY (TeamId) REFERENCES Teams(TeamId),
	CONSTRAINT FK_CompetitionTeams_RequestStatuses FOREIGN KEY (RequestStatusId) REFERENCES RequestStatuses(RequestStatusId)
);

CREATE TABLE Matches (
	MatchId int PRIMARY KEY IDENTITY(1, 1),
	HomeTeamId int NOT NULL,
	AwayTeamId int NOT NULL,
	CompetitionId int,
	[Time] datetime NOT NULL,
	Latitude real,
	Longitude real,
	[Length] int NOT NULL,
	PlayersPerTeam int NOT NULL,
	HomeTeamGoals int,
	AwayTeamGoals int,
	MatchStatusId int NOT NULL DEFAULT 1,
	CONSTRAINT FK_Matches_Teams_Home FOREIGN KEY (HomeTeamId) REFERENCES Teams(TeamId),
	CONSTRAINT FK_Matches_Teams_Away FOREIGN KEY (AwayTeamId) REFERENCES Teams(TeamId),
	CONSTRAINT FK_Matches_Competitions FOREIGN KEY (CompetitionId) REFERENCES Competitions(CompetitionId),
	CONSTRAINT FK_Matches_MatchStatuses FOREIGN KEY (MatchStatusId) REFERENCES MatchStatuses(MatchStatusId)
);