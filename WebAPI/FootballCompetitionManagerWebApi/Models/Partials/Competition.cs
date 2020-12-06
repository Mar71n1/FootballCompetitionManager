using FootballCompetitionManagerWebApi.Models.DTOs;

namespace FootballCompetitionManagerWebApi.Models
{
    public partial class Competition
    {
        public Competition(CompetitionDTO competitionDto)
        {
            CompetitionId = competitionDto.CompetitionId;
            Name = competitionDto.Name;
            NumberOfTeams = competitionDto.NumberOfTeams;
            MatchLength = competitionDto.MatchLength;
            PlayersPerTeam = competitionDto.PlayersPerTeam;
            OwnerId = competitionDto.OwnerId;
            Description = competitionDto.Description;
            CreationDate = competitionDto.CreationDate;
            CompetitionStatusId = competitionDto.CompetitionStatusId;
        }
    }
}