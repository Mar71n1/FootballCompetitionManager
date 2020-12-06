using FootballCompetitionManagerWebApi.Models.DTOs;

namespace FootballCompetitionManagerWebApi.Models
{
    public partial class LeagueSeason
    {
        public LeagueSeason() { }

        public LeagueSeason(LeagueSeasonDTO leagueSeasonDto)
        {
            LeagueSeasonId = leagueSeasonDto.LeagueSeasonId;
            CompetitionId = leagueSeasonDto.Competition.CompetitionId;
            DoubleMatches = leagueSeasonDto.DoubleMatches;
            Competition = new Competition(leagueSeasonDto.Competition);
        }
    }
}