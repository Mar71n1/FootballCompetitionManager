--Skrypt do uzupełnienia tabel słownikowych
INSERT INTO Roles
VALUES ('User'),
	   ('Administrator');
	   
INSERT INTO RequestStatuses
VALUES ('Pending'),
	   ('Accepted'),
	   ('Rejected');
	   
INSERT INTO MatchStatuses
VALUES ('Proposed by home team owner'),
	   ('Proposed by away team owner'),
	   ('Planned'),
	   ('Score proposed by home team owner'),
	   ('Score proposed by away team owner'),
	   ('Score proposed 2nd time by home team owner'),
	   ('Score proposed 2nd time by away team owner'),
	   ('Score accepted'),
	   ('Score unknown');

INSERT INTO CompetitionStatuses
VALUES ('Planning'),
	   ('In progress'),
	   ('Completed');