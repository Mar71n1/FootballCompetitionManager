using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System;
using System.Linq;
using System.Net;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/leagueseasons")]
    public class LeagueSeasonsController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("{leagueSeasonId}")]
        [HttpGet]
        public IHttpActionResult GetLeagueSeason(int leagueSeasonId)
        {
            LeagueSeason leagueSeason = dbContext.LeagueSeasons.Find(leagueSeasonId);

            if (leagueSeason == null)
                return Content(HttpStatusCode.NotFound, $"There is no league season with given id = {leagueSeasonId}");

            return Content(HttpStatusCode.OK, new LeagueSeasonDTO(leagueSeason));
        }

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetLeagueSeasonByCompetitionId(int competitionId)
        {
            LeagueSeason leagueSeason = dbContext.LeagueSeasons
                .FirstOrDefault(ls => ls.CompetitionId == competitionId);

            if (leagueSeason == null)
                return Content(HttpStatusCode.NotFound, $"There is no league season for competition id = {competitionId}");

            return Content(HttpStatusCode.OK, new LeagueSeasonDTO(leagueSeason));
        }

        [Route("create")]
        [HttpPost]
        public IHttpActionResult CreateLeagueSeason(LeagueSeason leagueSeason)
        {
            Competition competition = dbContext.Competitions
                .FirstOrDefault(c => c.Name.Equals(leagueSeason.Competition.Name));

            if (competition != null)
            {
                return Content(HttpStatusCode.BadRequest, "There is already competition with given name");
            }

            try
            {
                leagueSeason.Competition.CreationDate = DateTime.Now;
                leagueSeason.Competition.User = dbContext.Users.Find(leagueSeason.Competition.OwnerId);
                leagueSeason.Competition.CompetitionStatusId = CompetitionStatus.PLANNING;
                dbContext.Competitions.Add(leagueSeason.Competition);
                dbContext.LeagueSeasons.Add(leagueSeason);
                dbContext.SaveChanges();
            }
            catch (Exception exception)
            {
                throw exception;
            }

            return Content(HttpStatusCode.Created, new LeagueSeasonDTO(leagueSeason));
        }

        [Route("{leagueSeasonId}/update")]
        [HttpPut]
        public IHttpActionResult UpdateLeagueSeason(int leagueSeasonId, LeagueSeason updatedLeagueSeason)
        {
            LeagueSeason leagueSeason = dbContext.LeagueSeasons.Find(leagueSeasonId);

            if (leagueSeason == null)
            {
                return Content(HttpStatusCode.NotFound, $"There is no leagueSeason with id = {leagueSeasonId}");
            }
            else if (leagueSeason.Competition.CompetitionStatusId != CompetitionStatus.PLANNING)
            {
                return Content(HttpStatusCode.BadRequest, "You can only update leagueSeason when it is in planning phase.");
            }
            else if (leagueSeason.LeagueSeasonId != updatedLeagueSeason.LeagueSeasonId
                || leagueSeason.CompetitionId != updatedLeagueSeason.Competition.CompetitionId
                || leagueSeason.Competition.OwnerId != updatedLeagueSeason.Competition.OwnerId
                || leagueSeason.Competition.CreationDate != updatedLeagueSeason.Competition.CreationDate
                || leagueSeason.Competition.CompetitionStatusId != updatedLeagueSeason.Competition.CompetitionStatusId)
            {
                return Content(HttpStatusCode.BadRequest, $"You modified data that is not permitted to be changed");
            }
            else
            {
                leagueSeason.DoubleMatches = updatedLeagueSeason.DoubleMatches;
                leagueSeason.Competition.Name = updatedLeagueSeason.Competition.Name;
                leagueSeason.Competition.NumberOfTeams = updatedLeagueSeason.Competition.NumberOfTeams;
                leagueSeason.Competition.MatchLength = updatedLeagueSeason.Competition.MatchLength;
                leagueSeason.Competition.PlayersPerTeam = updatedLeagueSeason.Competition.PlayersPerTeam;
                leagueSeason.Competition.Description = updatedLeagueSeason.Competition.Description;
                dbContext.SaveChanges();
            }

            return Content(HttpStatusCode.OK, new LeagueSeasonDTO(leagueSeason));
        }

        [Route("{leagueSeasonId}/delete")]
        [HttpDelete]
        public IHttpActionResult DeleteLeagueSeason(int leagueSeasonId)
        {
            LeagueSeason leagueSeason = dbContext.LeagueSeasons.Find(leagueSeasonId);

            if (leagueSeason == null)
                return Content(HttpStatusCode.NotFound, $"There is no league season with id = {leagueSeasonId}");

            Competition competition = dbContext.Competitions.Find(leagueSeason.CompetitionId);

            IQueryable<CompetitionTeam> competitionTeams = dbContext.CompetitionTeams
                .Where(ct => ct.CompetitionId == competition.CompetitionId);
            IQueryable<Match> matches = dbContext.Matches
                .Where(m => m.CompetitionId == competition.CompetitionId);

            dbContext.Matches.RemoveRange(matches);
            dbContext.CompetitionTeams.RemoveRange(competitionTeams);
            dbContext.Competitions.Remove(competition);
            dbContext.LeagueSeasons.Remove(leagueSeason);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }
    }
}
