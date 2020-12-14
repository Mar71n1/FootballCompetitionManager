using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/competitions")]
    public class CompetitionsController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("{competitionId}")]
        [HttpGet]
        public IHttpActionResult GetCompetition(int competitionId)
        {
            Competition competition = dbContext.Competitions.Find(competitionId);

            if (competition == null)
                return Content(HttpStatusCode.NotFound, $"Competition id = {competitionId} not found");

            return Content(HttpStatusCode.OK, new CompetitionDTO(competition));
        }

        public IHttpActionResult GetCompetitions()
        {
            IQueryable<Competition> competitions = dbContext.Competitions;

            return Content(HttpStatusCode.OK, getCompetitionsDto(competitions));
        }

        [Route("foruser")]
        [HttpGet]
        public IHttpActionResult GetCompetitionsForUser(int userId)
        {
            IQueryable<int> userTeamsIds = dbContext.Teams
                .Where(team => team.OwnerId == userId)
                .Select(team => team.TeamId)
                .Union(
                    dbContext.TeamUsers
                    .Where(teamUser => teamUser.UserId == userId && teamUser.RequestStatusId == RequestStatus.ACCEPTED)
                    .Select(teamUser => teamUser.TeamId)
                );

            IQueryable<Competition> competitions = dbContext.Competitions
                .Where(competition => competition.OwnerId == userId)
                .Union(
                    dbContext.CompetitionTeams
                    .Where(ct => userTeamsIds.Contains(ct.TeamId) && ct.RequestStatusId == RequestStatus.ACCEPTED)
                    .Select(ct => ct.Competition)
                );

            return Content(HttpStatusCode.OK, getCompetitionsDto(competitions));
        }

        [Route("byteam")]
        [HttpGet]
        public IHttpActionResult GetCompetitionsByTeam(int teamId)
        {
            IQueryable<Competition> competitions = dbContext.CompetitionTeams
                .Where(ct => ct.TeamId == teamId && ct.RequestStatusId == RequestStatus.ACCEPTED)
                .Select(ct => ct.Competition);
            return Content(HttpStatusCode.OK, getCompetitionsDto(competitions));
        }

        [Route("search")]
        [HttpGet]
        public IHttpActionResult SearchCompetitionsByName(string name)
        {
            IQueryable<Competition> competitions = dbContext.Competitions
                .Where(competition => competition.Name.Contains(name))
                .OrderBy(competition => competition.Name);
            return Content(HttpStatusCode.OK, getCompetitionsDto(competitions));
        }

        [Route("{competitionId}/start")]
        [HttpPost]
        public IHttpActionResult StartCompetition(int competitionId)
        {
            Competition competition = dbContext.Competitions.Find(competitionId);

            if (competition == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition id = {competitionId}");

            if (competition.CompetitionStatusId != CompetitionStatus.PLANNING)
                return Content(HttpStatusCode.BadRequest, $"Competition id = {competitionId} is not in planning phase");

            int competitionTeamsQty = dbContext.CompetitionTeams
                .Count(ct => ct.CompetitionId == competitionId && ct.RequestStatusId == RequestStatus.ACCEPTED);
            if (competitionTeamsQty != competition.NumberOfTeams)
                return Content(HttpStatusCode.BadRequest, $"To start competition id = {competitionId} {competition.NumberOfTeams} teams are needed. Now there are {competitionTeamsQty} teams");

            competition.CompetitionStatusId = CompetitionStatus.IN_PROGRESS;
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("{competitionId}/finish")]
        [HttpPost]
        public IHttpActionResult FinishCompetition(int competitionId)
        {
            Competition competition = dbContext.Competitions.Find(competitionId);

            if (competition == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition id = {competitionId}");

            if (competition.CompetitionStatusId != CompetitionStatus.IN_PROGRESS)
                return Content(HttpStatusCode.BadRequest, $"Competition id = {competitionId} is not in progress");

            IQueryable<Match> plannedMatches = dbContext.Matches
                .Where(m => m.CompetitionId == competitionId
                && (m.MatchStatusId == MatchStatus.PLANNED
                || m.MatchStatusId == MatchStatus.SCORE_UNKNOWN));

            competition.CompetitionStatusId = CompetitionStatus.COMPLETED;
            dbContext.Matches.RemoveRange(plannedMatches);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        private IList<CompetitionDTO> getCompetitionsDto(IEnumerable<Competition> competitions)
        {
            IList<CompetitionDTO> competitionsDto = new List<CompetitionDTO>();
            foreach (Competition competition in competitions)
                competitionsDto.Add(new CompetitionDTO(competition));
            return competitionsDto;
        }
    }
}