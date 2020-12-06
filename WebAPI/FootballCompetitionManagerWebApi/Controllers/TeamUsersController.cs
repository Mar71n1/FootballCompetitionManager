using FootballCompetitionManagerWebApi.Models;
using System.Linq;
using System.Net;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/teamusers")]
    public class TeamUsersController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("status")]
        [HttpGet]
        public IHttpActionResult GetActualRequestStatus(int teamId, int userId)
        {
            TeamUser teamUser = dbContext.TeamUsers
                .FirstOrDefault(tu => tu.TeamId == teamId && tu.UserId == userId);

            if (teamUser == null)
                return StatusCode(HttpStatusCode.NotFound);
            else
                return Content(HttpStatusCode.OK, teamUser.RequestStatusId);
        }

        [Route("cancelrequest")]
        [HttpDelete]
        public IHttpActionResult CancelRequest(int teamId, int userId)
        {
            TeamUser teamUser = dbContext.TeamUsers
                .FirstOrDefault(tu => tu.TeamId == teamId && tu.UserId == userId);

            if (teamUser == null)
                return Content(HttpStatusCode.NotFound, $"User id = {userId} for team id = {teamId} has no status");
            else if (teamUser.RequestStatusId != RequestStatus.PENDING)
                return Content(HttpStatusCode.BadRequest, $"User id = {userId} status for team id = {teamId} is not PENDING");

            dbContext.TeamUsers.Remove(teamUser);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("leaveteam")]
        [HttpDelete]
        public IHttpActionResult LeaveTeam(int teamId, int userId)
        {
            TeamUser teamUser = dbContext.TeamUsers
                .FirstOrDefault(tu => tu.TeamId == teamId && tu.UserId == userId);

            if (teamUser == null)
                return Content(HttpStatusCode.BadRequest, $"User id = {userId} for team id = {teamId} has no status");
            else if (teamUser.RequestStatusId != RequestStatus.ACCEPTED)
                return Content(HttpStatusCode.BadRequest, $"User id = {userId} status for team id = {teamId} is not ACCEPTED");

            dbContext.TeamUsers.Remove(teamUser);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("acceptuserrequest")]
        [HttpPost]
        public IHttpActionResult AcceptUserRequest(int teamId, int userId)
        {
            TeamUser teamUser = GetPendingRequest(teamId, userId);

            if (teamUser == null)
                return Content(HttpStatusCode.NotFound, $"There is no pending request for team with id = {teamId} and user with id = {userId}");

            teamUser.RequestStatusId = RequestStatus.ACCEPTED;
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("rejectuserrequest")]
        [HttpPost]
        public IHttpActionResult RejectUserRequest(int teamId, int userId)
        {
            TeamUser teamUser = GetPendingRequest(teamId, userId);

            if (teamUser == null)
                return Content(HttpStatusCode.NotFound, $"There is no pending request for team with id = {teamId} and user with id = {userId}");

            teamUser.RequestStatusId = RequestStatus.REJECTED;
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        private TeamUser GetPendingRequest(int teamId, int userId)
        {
            return dbContext.TeamUsers
                .FirstOrDefault(tu => tu.TeamId == teamId && tu.UserId == userId && tu.RequestStatusId == RequestStatus.PENDING);
        }
    }
}