using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/matches")]
    public class MatchesController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext;

        private static DateTime lastCheck;

        public MatchesController()
        {
            dbContext = new FootballCompetitionManagerDbContext();
            CheckMatches();
        }

        [Route("{matchId}")]
        [HttpGet]
        public IHttpActionResult GetMatch(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"There is no match with given id = {matchId}");

            return Content(HttpStatusCode.OK, new MatchDTO(match));
        }

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetMatches()
        {
            IQueryable<Match> matches = dbContext.Matches;
            return Content(HttpStatusCode.OK, getMatchesDto(matches));
        }

        [Route("foruser")]
        [HttpGet]
        public IHttpActionResult GetMatchesForUser(int userId)
        {
            IQueryable<int> userTeamsIds = dbContext.TeamUsers
                .Where(teamUser => teamUser.UserId == userId && teamUser.RequestStatusId == RequestStatus.ACCEPTED)
                .Select(teamUser => teamUser.TeamId);

            IQueryable<Match> matches = dbContext.Matches
                .Where(match => (userTeamsIds.Contains(match.HomeTeamId) || userTeamsIds.Contains(match.AwayTeamId))
                && (match.MatchStatusId == MatchStatus.PLANNED
                || match.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER))
                .OrderBy(m => m.Time);

            return Content(HttpStatusCode.OK, getMatchesDto(matches));
        }

        [Route("incomingbyteam")]
        [HttpGet]
        public IHttpActionResult GetIncomingMatchesByTeam(int teamId)
        {
            IQueryable<Match> matches = dbContext.Matches
                .Where(m => (m.HomeTeamId == teamId || m.AwayTeamId == teamId)
                && (m.MatchStatusId == MatchStatus.PLANNED
                || m.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER))
                .OrderBy(m => m.Time);

            return Content(HttpStatusCode.OK, getMatchesDto(matches));
        }

        [Route("latestresultsbyteam")]
        [HttpGet]
        public IHttpActionResult GetLatestResultsByTeam(int teamId)
        {
            IQueryable<Match> results = dbContext.Matches
                .Where(m => (m.HomeTeamId == teamId || m.AwayTeamId == teamId)
                && (m.MatchStatusId == MatchStatus.SCORE_ACCEPTED
                || m.MatchStatusId == MatchStatus.SCORE_UNKNOWN
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER))
                .OrderByDescending(m => m.Time);

            return Content(HttpStatusCode.OK, getMatchesDto(results));

        }

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetCompetitionMatches(int competitionId)
        {
            if (dbContext.Competitions.Find(competitionId) == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition id = {competitionId}");

            IQueryable<Match> results = dbContext.Matches
                .Where(m => m.CompetitionId == competitionId 
                && (m.MatchStatusId == MatchStatus.PLANNED
                || m.MatchStatusId == MatchStatus.SCORE_UNKNOWN))
                .OrderBy(m => m.Time);

            return Content(HttpStatusCode.OK, getMatchesDto(results));
        }

        [Route("results")]
        [HttpGet]
        public IHttpActionResult GetCompetitionResults(int competitionId)
        {
            if (dbContext.Competitions.Find(competitionId) == null)
                return Content(HttpStatusCode.NotFound, $"There is no competition id = {competitionId}");

            IQueryable<Match> results = dbContext.Matches
                .Where(m => m.CompetitionId == competitionId
                && m.MatchStatusId == MatchStatus.SCORE_ACCEPTED)
                .OrderByDescending(m => m.Time);

            return Content(HttpStatusCode.OK, getMatchesDto(results));
        }

        [Route("create")]
        [HttpPost]
        public IHttpActionResult CreateMatch(Match match)
        {
            if (match.HomeTeamId == match.AwayTeamId)
                return Content(HttpStatusCode.BadRequest, "Only two different teams can play match against each other.");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if (match.CompetitionId != null && dbContext.Competitions.Find(match.CompetitionId).OwnerId == loggedUser.UserId)
                match.MatchStatusId = MatchStatus.PLANNED;
            else if (match.CompetitionId == null && dbContext.Teams.Find(match.HomeTeamId).OwnerId == loggedUser.UserId)
                match.MatchStatusId = MatchStatus.PROPOSED_HOME_TEAM_OWNER;
            else if (match.CompetitionId == null && dbContext.Teams.Find(match.AwayTeamId).OwnerId == loggedUser.UserId)
                match.MatchStatusId = MatchStatus.PROPOSED_AWAY_TEAM_OWNER;
            else
                return Content(HttpStatusCode.BadRequest, "Wrong request");

            dbContext.Matches.Add(match);
            dbContext.SaveChanges();

            return Content(HttpStatusCode.Created, new MatchDTO(match));
        }

        [Route("{matchId}")]
        [HttpPut]
        public IHttpActionResult UpdateMatch(int matchId, Match updatedMatch)
        {
            Match match = dbContext.Matches.Find(matchId);
            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if (loggedUser.Roles.Select(role => role.RoleId).Contains(Role.ADMINISTRATOR))
            {
                match.Time = updatedMatch.Time;
                match.Latitude = updatedMatch.Latitude;
                match.Longitude = updatedMatch.Longitude;
                match.Length = updatedMatch.Length;
                match.PlayersPerTeam = updatedMatch.PlayersPerTeam;
                dbContext.SaveChanges();

                return Content(HttpStatusCode.OK, new MatchDTO(match));
            }
            else
            {
                return Content(HttpStatusCode.BadRequest, "User is not admin");
            }
        }

        [Route("{matchId}")]
        [HttpDelete]
        public IHttpActionResult DeleteMatch(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            dbContext.Matches.Remove(match);
            dbContext.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        [Route("{matchId}/setscore")]
        [HttpPut]
        public IHttpActionResult SetMatchScore(int matchId, int homeTeamGoals, int awayTeamGoals)
        {
            Match match = dbContext.Matches.Find(matchId);

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");
            else if (match.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER
                || match.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER
                || (match.MatchStatusId == MatchStatus.PLANNED && DateTime.Now < match.Time.AddMinutes(match.Length)))
                return Content(HttpStatusCode.BadRequest, $"Cannot set score of match with status {match.MatchStatusId}");

            if (loggedUser.Roles.Select(role => role.RoleId).Contains(Role.ADMINISTRATOR))
            {
                match.MatchStatusId = MatchStatus.SCORE_ACCEPTED;
            }
            else if (match.Competition == null
                && match.MatchStatusId == MatchStatus.PLANNED
                && (match.Team1.OwnerId == loggedUser.UserId || match.Team.OwnerId == loggedUser.UserId))
            {
                if (match.Team1.OwnerId == loggedUser.UserId)
                    match.MatchStatusId = MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER;
                else if (match.Team.OwnerId == loggedUser.UserId)
                    match.MatchStatusId = MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER;
            }
            else if (match.Competition == null
                && match.MatchStatusId == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER
                && match.Team.OwnerId == loggedUser.UserId)
            {
                match.MatchStatusId = MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER;
            }
            else if (match.Competition == null
                && match.MatchStatusId == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER
                && match.Team1.OwnerId == loggedUser.UserId)
            {
                match.MatchStatusId = MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER;
            }
            else if (match.MatchStatusId == MatchStatus.PLANNED && match.Competition != null && match.Competition.OwnerId == loggedUser.UserId)
            {
                match.MatchStatusId = MatchStatus.SCORE_ACCEPTED;
            }
            else
            {
                return Content(HttpStatusCode.BadRequest, "This user cannot set score match");
            }

            match.HomeTeamGoals = homeTeamGoals;
            match.AwayTeamGoals = awayTeamGoals;
            dbContext.SaveChanges();

            return Content(HttpStatusCode.OK, new MatchDTO(match));
        }

        [Route("{matchId}/acceptscore")]
        [HttpPut]
        public IHttpActionResult AcceptMatchScore(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if ((match.MatchStatusId == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER && match.Team.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER && match.Team1.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER && match.Team.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER && match.Team1.OwnerId == loggedUser.UserId))
            {
                match.MatchStatusId = MatchStatus.SCORE_ACCEPTED;
            }
            else
                return Content(HttpStatusCode.BadRequest, "Cannot accept match score");

            dbContext.SaveChanges();
            return Content(HttpStatusCode.OK, new MatchDTO(match));
        }

        [Route("{matchId}/rejectscore")]
        [HttpPut]
        public IHttpActionResult RejectMatchScore(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if ((match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER && match.Team.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER && match.Team1.OwnerId == loggedUser.UserId))
            {
                match.HomeTeamGoals = null;
                match.AwayTeamGoals = null;
                match.MatchStatusId = MatchStatus.SCORE_UNKNOWN;
            }
            else
                return Content(HttpStatusCode.BadRequest, "Cannot reject match score");

            dbContext.SaveChanges();
            return Content(HttpStatusCode.OK, new MatchDTO(match));
        }

        [Route("{matchId}/accept")]
        [HttpPut]
        public IHttpActionResult AcceptMatchProposal(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if ((match.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER && match.Team.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER && match.Team1.OwnerId == loggedUser.UserId))
                match.MatchStatusId = MatchStatus.PLANNED;
            else
                return Content(HttpStatusCode.BadRequest, $"Bad request for match id = {matchId}");

            dbContext.SaveChanges();
            return Content(HttpStatusCode.OK, new MatchDTO(match));
        }

        [Route("{matchId}/reject")]
        [HttpDelete]
        public IHttpActionResult RejectMatchProposal(int matchId)
        {
            Match match = dbContext.Matches.Find(matchId);

            if (match == null)
                return Content(HttpStatusCode.NotFound, $"No match id = {matchId}");

            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            if ((match.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER && match.Team.OwnerId == loggedUser.UserId)
                || (match.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER && match.Team1.OwnerId == loggedUser.UserId))
                dbContext.Matches.Remove(match);
            else
                return Content(HttpStatusCode.BadRequest, $"Bad request for match id = {matchId}");

            dbContext.SaveChanges();
            return StatusCode(HttpStatusCode.NoContent);
        }

        private void CheckMatches()
        {
            if (lastCheck < DateTime.Now.AddHours(-1))
            {
                CheckWeekOldScores();
                CheckPlannedMatches();
                lastCheck = DateTime.Now;
            }
        }

        private void CheckWeekOldScores()
        {
            DateTime weekAgo = DateTime.Now.AddDays(-7);

            IQueryable<Match> matches = dbContext.Matches.Where(m => m.MatchStatusId == MatchStatus.PLANNED && m.Time < weekAgo);

            foreach (Match match in matches)
                match.MatchStatusId = MatchStatus.SCORE_UNKNOWN;
            dbContext.SaveChanges();

            matches = dbContext.Matches
                .Where(m => (m.MatchStatusId == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER 
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER)
                && m.Time < weekAgo);

            foreach (Match match in matches)
                match.MatchStatusId = MatchStatus.SCORE_ACCEPTED;
            dbContext.SaveChanges();
        }

        private void CheckPlannedMatches()
        {
            DateTime tomorrow = DateTime.Now.AddDays(1);

            IQueryable<Match> matches = dbContext.Matches
                .Where(m => m.Time < tomorrow
                && (m.MatchStatusId == MatchStatus.PROPOSED_HOME_TEAM_OWNER
                || m.MatchStatusId == MatchStatus.PROPOSED_AWAY_TEAM_OWNER));

            dbContext.Matches.RemoveRange(matches);
            dbContext.SaveChanges();
        }

        private IList<MatchDTO> getMatchesDto(IEnumerable<Match> matches)
        {
            IList<MatchDTO> matchesDto = new List<MatchDTO>();
            foreach (Match match in matches)
                matchesDto.Add(new MatchDTO(match));
            return matchesDto;
        }
    }
}
