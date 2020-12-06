using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class MatchDTO
    {
        public MatchDTO(Match match)
        {
            MatchId = match.MatchId;

            if (match.Competition != null)
            {
                CompetitionId = match.Competition.CompetitionId;
                CompetitionName = match.Competition.Name;
            }
            else
            {
                CompetitionId = null;
                CompetitionName = null;
            }
            
            HomeTeamId = match.HomeTeamId;
            if (match.Team1 != null)
                HomeTeamName = match.Team1.Name;
            
            AwayTeamId = match.AwayTeamId;
            if (match.Team != null)
                AwayTeamName = match.Team.Name;
            
            Time = match.Time;
            Latitude = match.Latitude;
            Longitude = match.Longitude;
            Length = match.Length;
            PlayersPerTeam = match.PlayersPerTeam;
            HomeTeamGoals = match.HomeTeamGoals;
            AwayTeamGoals = match.AwayTeamGoals;
            MatchStatusId = match.MatchStatusId;
        }

        public int MatchId { get; set; }
        public int? CompetitionId { get; set; }
        public string CompetitionName { get; set; }
        public int HomeTeamId { get; set; }
        public string HomeTeamName { get; set; }
        public int AwayTeamId { get; set; }
        public string AwayTeamName { get; set; }
        public DateTime Time { get; set; }
        public float? Latitude { get; set; }
        public float? Longitude { get; set; }
        public int Length { get; set; }
        public int PlayersPerTeam { get; set; }
        public int? HomeTeamGoals { get; set; }
        public int? AwayTeamGoals { get; set; }
        public int MatchStatusId { get; set; }
    }
}