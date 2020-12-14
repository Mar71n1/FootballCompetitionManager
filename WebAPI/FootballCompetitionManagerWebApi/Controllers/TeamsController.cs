using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System;
using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/teams")]
    public class TeamsController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        public IHttpActionResult Get(int id)
        {
            Team team = dbContext.Teams.Find(id);

            if (team == null)
                return Content(HttpStatusCode.NotFound, $"There is no team with given id = {id}");

            return Content(HttpStatusCode.OK, new TeamDTO(team));
        }


        public IHttpActionResult GetTeams()
        {
            IQueryable<Team> teams = dbContext.Teams;
            return Content(HttpStatusCode.OK, getTeamsDto(teams));
        }

        [Route("{teamId}/update")]
        [HttpPut]
        public IHttpActionResult UpdateTeam(int teamId, TeamDTO teamDto)
        {
            Team team = dbContext.Teams.Find(teamId);

            if (team == null)
            {
                return Content(HttpStatusCode.NotFound, $"There is no team with id = {teamId}");
            }
            else if (team.TeamId != teamDto.TeamId || team.OwnerId != teamDto.OwnerId || team.CreationDate != teamDto.CreationDate)
            {
                return Content(HttpStatusCode.BadRequest, $"You modified data that is not permitted to be changed");
            }
            
            try
            {
                team.Name = teamDto.Name;
                team.Description = teamDto.Description;
                dbContext.SaveChanges();
            }
            catch (DbUpdateException)
            {
                return Content(HttpStatusCode.BadRequest, "Duplicated team name. Team names must be unique");
            }

            return Content(HttpStatusCode.OK, new TeamDTO(team));
        }

        [Route("{teamId}/delete")]
        [HttpDelete]
        public IHttpActionResult DeleteTeam(int teamId)
        {
            Team team = dbContext.Teams.Find(teamId);

            if (team == null)
                return Content(HttpStatusCode.NotFound, $"There is no team with id = {teamId}");

            IQueryable<TeamUser> teamUsers = dbContext.TeamUsers.Where(tu => tu.TeamId == teamId);
            IQueryable<CompetitionTeam> competitionTeams = dbContext.CompetitionTeams.Where(ct => ct.TeamId == teamId);
            IQueryable<Match> matches = dbContext.Matches.Where(m => m.HomeTeamId == teamId || m.AwayTeamId == teamId);

            dbContext.TeamUsers.RemoveRange(teamUsers);
            dbContext.CompetitionTeams.RemoveRange(competitionTeams);
            dbContext.Matches.RemoveRange(matches);
            dbContext.Teams.Remove(team);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("foruser")]
        [HttpGet]
        public IHttpActionResult GetTeamsForUser(int userId)
        {
            IQueryable<Team> teams = dbContext.Teams
                .Where(team => team.OwnerId == userId)
                .Union(
                    dbContext.TeamUsers
                    .Where(teamUser => teamUser.UserId == userId && teamUser.RequestStatusId == RequestStatus.ACCEPTED)
                    .Select(teamUser => teamUser.Team)
                );
            
            IList<Team> teamsList = teams.ToList();

            if (!teamsList.Any())
                return Content(HttpStatusCode.NotFound, $"There are no teams for user with id = {userId}");

            return Content(HttpStatusCode.OK, getTeamsDto(teamsList));
        }

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetOwnerTeams(int ownerId)
        {
            if (dbContext.Users.Find(ownerId) == null)
                return Content(HttpStatusCode.NotFound, $"There is no user id = {ownerId}");

            IQueryable<Team> teams = dbContext.Teams
                .Where(t => t.OwnerId == ownerId);

            return Content(HttpStatusCode.OK, getTeamsDto(teams));
        }

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetCompetitionTeams(int competitionId)
        {
            if (dbContext.Competitions.Find(competitionId) == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition id = {competitionId}");

            IQueryable<Team> teams = dbContext.CompetitionTeams
                .Where(ct => ct.CompetitionId == competitionId
                && ct.RequestStatusId == RequestStatus.ACCEPTED)
                .Select(ct => ct.Team);

            return Content(HttpStatusCode.OK, getTeamsDto(teams));
        }

        [Route("search")]
        [HttpGet]
        public IHttpActionResult SearchTeamsByName(string name)
        {
            IQueryable<Team> teams = dbContext.Teams
                .Where(team => team.Name.Contains(name))
                .OrderBy(team => team.Name);
            return Content(HttpStatusCode.OK, getTeamsDto(teams));
        }

        [Route("pendingrequeststeamsforcompetition")]
        [HttpGet]
        public IHttpActionResult GetPendingRequestsTeamsForCompetition(int competitionId)
        {
            IQueryable<Team> teams = dbContext.CompetitionTeams
                .Where(ct => ct.CompetitionId == competitionId
                && ct.RequestStatusId == RequestStatus.PENDING)
                .Select(ct => ct.Team);
            return Content(HttpStatusCode.OK, getTeamsDto(teams));
        }

        [Route("create")]
        [HttpPost]
        public IHttpActionResult CreateTeam(Team team)
        {
            // Przypisanie zautentykowanego użytkownika jako właściciela drużyny
            string username = HttpContext.Current.User.Identity.Name;
            User user = dbContext.Users.First(u => u.Username.Equals(username));
            team.OwnerId = user.UserId;
            team.CreationDate = DateTime.Now;

            try
            {
                dbContext.Teams.Add(team);
                dbContext.SaveChanges();
            }
            catch (DbUpdateException)
            {
                return Content(HttpStatusCode.BadRequest, "Duplicated team name. Team names must be unique");
            }
            
            AddUserToTeam(team, user, true);
            return Content(HttpStatusCode.OK, new TeamDTO(team));
        }

        [Route("{teamId}/sendrequesttojoin")]
        [HttpPost]
        public IHttpActionResult SendRequestToJoinTeam(int teamId)
        {
            string username = HttpContext.Current.User.Identity.Name;
            User user = dbContext.Users.First(u => u.Username.Equals(username));
            Team team = dbContext.Teams.Find(teamId);

            bool success;
            try
            {
                success = AddUserToTeam(team, user, false);
            }
            catch (DbUpdateException)
            {
                return Content(HttpStatusCode.BadRequest, $"User {user.UserId} already in team {team.TeamId}");
            }

            return Content(HttpStatusCode.OK, success);
        }

        private bool AddUserToTeam(Team team, User user, bool isOwner)
        {
            TeamUser teamUser;

            teamUser = dbContext.TeamUsers.FirstOrDefault(tu => tu.TeamId == team.TeamId && tu.UserId == user.UserId && tu.RequestStatusId == RequestStatus.REJECTED);

            if (teamUser == null)
            {
                teamUser = new TeamUser()
                {
                    Team = team,
                    User = user
                };
                dbContext.TeamUsers.Add(teamUser);
            }

            if (isOwner)
                teamUser.RequestStatusId = RequestStatus.ACCEPTED;
            else
                teamUser.RequestStatusId = RequestStatus.PENDING;

            try
            {
                dbContext.SaveChanges();
            }
            catch (DbUpdateException)
            {
                throw;
            }

            return true;
        }

        private IList<TeamDTO> getTeamsDto(IEnumerable<Team> teams)
        {
            IList<TeamDTO> teamsDto = new List<TeamDTO>();
            foreach (Team team in teams)
                teamsDto.Add(new TeamDTO(team));
            return teamsDto;
        }
    }
}
