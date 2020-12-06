using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Threading;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/users")]
    public class UsersController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("register")]
        [HttpPost]
        [AllowAnonymous]
        public IHttpActionResult Register(UserToRegister userToRegister)
        {
            if (userToRegister.ValidatePassword())
            {
                User newUser = new User(userToRegister);
                Role userRole = dbContext.Roles.Find(1);
                newUser.Roles.Add(userRole);

                try
                {
                    dbContext.Users.Add(newUser);
                    dbContext.SaveChanges();
                }
                catch (DbUpdateException)
                {
                    return Content(HttpStatusCode.BadRequest, "There is already an account for given e-mail address or username.");
                }
                
                return Content(HttpStatusCode.OK, new UserDTO(newUser));
            }
            else
            {
                return Content(HttpStatusCode.BadRequest, "Passwords don't match.");
            }
        }

        [Route("login")]
        [HttpGet]
        public IHttpActionResult Login()
        {
            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(user => user.Username.Equals(username)).FirstOrDefault();

            return Content(HttpStatusCode.OK, new UserDTO(loggedUser));
        }

        [Route("{userId}")]
        [HttpPut]
        public IHttpActionResult ChangeData(int userId, User updatedUser)
        {
            string username = Thread.CurrentPrincipal.Identity.Name;
            User loggedUser = dbContext.Users.Where(u => u.Username.Equals(username)).FirstOrDefault();

            if (loggedUser.UserId != userId)
                return Content(HttpStatusCode.BadRequest, "Cannot change other user data");

            User user = dbContext.Users.Find(userId);

            if (user == null)
            {
                return Content(HttpStatusCode.NotFound, $"There is no user with id = {userId}");
            }
            else if (user.UserId != updatedUser.UserId
                || user.Username != updatedUser.Username
                || user.CreationDate != updatedUser.CreationDate)
            {
                return Content(HttpStatusCode.BadRequest, $"You modified data that is not permitted to be changed");
            }
            else
            {
                try
                {
                    user.Email = updatedUser.Email;
                    user.Password = updatedUser.Password;
                    dbContext.SaveChanges();
                }
                catch (DbUpdateException)
                {
                    return Content(HttpStatusCode.BadRequest, "Duplicated e-mail. E-mail must be unique");
                }
            }

            return Content(HttpStatusCode.OK, new UserDTO(user));
        }

        [Route("byteam")]
        [HttpGet]
        public IHttpActionResult GetUsersByTeam(int teamId)
        {
            IQueryable<User> players = dbContext.TeamUsers
                .Where(teamUser => teamUser.TeamId == teamId && teamUser.RequestStatusId == RequestStatus.ACCEPTED)
                .Select(teamUser => teamUser.User);
            return Content(HttpStatusCode.OK, getUsersDto(players));
        }

        [Route("pendingrequestsusersforteam")]
        [HttpGet]
        public IHttpActionResult GetPendingRequestsUsersForTeam(int teamId)
        {
            IQueryable<User> users = dbContext.TeamUsers
                .Where(teamUser => teamUser.TeamId == teamId 
                && teamUser.RequestStatusId == RequestStatus.PENDING)
                .Select(teamUser => teamUser.User);
            return Content(HttpStatusCode.OK, getUsersDto(users));
        }

        [NonAction]
        public User GetUserByUsername(string username)
        {
            return dbContext.Users.Where(u => u.Username.Equals(username)).FirstOrDefault();
        }

        private IList<UserDTO> getUsersDto(IEnumerable<User> users)
        {
            IList<UserDTO> usersDto = new List<UserDTO>();
            foreach (User user in users)
                usersDto.Add(new UserDTO(user));
            return usersDto;
        }
    }
}
