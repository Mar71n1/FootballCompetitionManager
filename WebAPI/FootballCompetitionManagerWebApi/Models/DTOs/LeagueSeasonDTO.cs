namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class LeagueSeasonDTO
    {
        public int LeagueSeasonId { get; set; }
        public bool DoubleMatches { get; set; }
        public CompetitionDTO Competition { get; set; }

        public LeagueSeasonDTO(LeagueSeason leagueSeason)
        {
            LeagueSeasonId = leagueSeason.LeagueSeasonId;
            DoubleMatches = leagueSeason.DoubleMatches;
            Competition = new CompetitionDTO(leagueSeason.Competition);
        }
    }
}