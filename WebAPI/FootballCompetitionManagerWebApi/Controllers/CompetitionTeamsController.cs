using FootballCompetitionManagerWebApi.Models;
using System.Linq;
using System.Net;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/competitionteams")]
    public class CompetitionTeamsController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("sendrequesttojoin")]
        [HttpPost]
        public IHttpActionResult SendRequestToJoinCompetition(int competitionId, int teamId)
        {
            Competition competition = dbContext.Competitions.Find(competitionId);
            if (competition == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition with id = {competitionId}");

            Team team = dbContext.Teams.Find(teamId);
            if (team == null)
                return Content(HttpStatusCode.NotFound, $"There is no team with id = {teamId}");

            CompetitionTeam competitionTeam = dbContext.CompetitionTeams
                .FirstOrDefault(ct => ct.CompetitionId == competitionId && ct.TeamId == teamId);

            if (competitionTeam == null)
            {
                competitionTeam = new CompetitionTeam()
                {
                    Competition = competition,
                    Team = team,
                };
                dbContext.CompetitionTeams.Add(competitionTeam);
            }
            else if (competitionTeam.RequestStatusId == RequestStatus.ACCEPTED)
                return Content(HttpStatusCode.NotFound, $"Team id = {teamId} is already accepted for competition id = {competitionId}");

            competitionTeam.RequestStatusId = RequestStatus.PENDING;
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("cancelrequest")]
        [HttpDelete]
        public IHttpActionResult CancelTeamRequest(int competitionId, int teamId)
        {
            CompetitionTeam competitionTeam = dbContext.CompetitionTeams
                .FirstOrDefault(ct => ct.CompetitionId == competitionId && ct.TeamId == teamId);

            if (competitionTeam == null)
                return Content(HttpStatusCode.BadRequest, $"Team id = {teamId} for competition id = {competitionId} has no status");
            else if (competitionTeam.RequestStatusId != RequestStatus.PENDING)
                return Content(HttpStatusCode.BadRequest, $"Team id = {teamId} status for competition id = {competitionId} is not PENDING");

            dbContext.CompetitionTeams.Remove(competitionTeam);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("remove")]
        [HttpDelete]
        public IHttpActionResult RemoveTeamFromCompetition(int competitionId, int teamId)
        {
            CompetitionTeam competitionTeam = dbContext.CompetitionTeams
                .FirstOrDefault(ct => ct.CompetitionId == competitionId && ct.TeamId == teamId);

            if (competitionTeam == null)
                return Content(HttpStatusCode.NotFound, $"Team id = {teamId} for competition id = {competitionId} has no status");
            else if (competitionTeam.RequestStatusId != RequestStatus.ACCEPTED)
                return Content(HttpStatusCode.BadRequest, $"Team id = {teamId} status for competition id = {competitionId} is not ACCEPTED");

            dbContext.CompetitionTeams.Remove(competitionTeam);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("acceptrequest")]
        [HttpPost]
        public IHttpActionResult AcceptTeamRequest(int competitionId, int teamId) => ResolveRequest(competitionId, teamId, true);

        [Route("rejectrequest")]
        [HttpPost]
        public IHttpActionResult RejectTeamRequest(int competitionId, int teamId) => ResolveRequest(competitionId, teamId, false);

        private IHttpActionResult ResolveRequest(int competitionId, int teamId, bool positiveDescision)
        {
            CompetitionTeam competitionTeam = dbContext.CompetitionTeams
                .FirstOrDefault(ct => ct.CompetitionId == competitionId && ct.TeamId == teamId && ct.RequestStatusId == RequestStatus.PENDING);

            if (competitionTeam == null)
                return Content(HttpStatusCode.NotFound, $"There is no pending request for competition with id = {competitionId} and team with id = {teamId}");

            if (positiveDescision)
                competitionTeam.RequestStatusId = RequestStatus.ACCEPTED;
            else
                competitionTeam.RequestStatusId = RequestStatus.REJECTED;

            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }
    }
}