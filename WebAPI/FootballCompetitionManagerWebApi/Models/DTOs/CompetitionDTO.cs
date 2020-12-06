using System;

namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class CompetitionDTO
    {
        public int CompetitionId { get; set; }
        public string Name { get; set; }
        public int NumberOfTeams { get; set; }
        public int MatchLength { get; set; }
        public int PlayersPerTeam { get; set; }
        public int OwnerId { get; set; }
        public string OwnerUsername { get; set; }
        public string Description { get; set; }
        public DateTime CreationDate { get; set; }
        public int CompetitionStatusId { get; set; }

        public CompetitionDTO(Competition competition)
        {
            CompetitionId = competition.CompetitionId;
            Name = competition.Name;
            NumberOfTeams = competition.NumberOfTeams;
            MatchLength = competition.MatchLength;
            PlayersPerTeam = competition.PlayersPerTeam;
            OwnerId = competition.OwnerId;
            OwnerUsername = competition.User.Username;
            Description = competition.Description;
            CreationDate = competition.CreationDate;
            CompetitionStatusId = competition.CompetitionStatusId;
        }
    }
}