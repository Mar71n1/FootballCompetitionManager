using System;
using System.Collections.Generic;

namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class UserDTO
    {
        public UserDTO(User user)
        {
            UserId = user.UserId;
            Email = user.Email;
            Username = user.Username;
            Password = user.Password;
            CreationDate = user.CreationDate;

            RolesIds = new List<int>();
            foreach (Role role in user.Roles)
            {
                RolesIds.Add(role.RoleId);
            }
        }

        public int UserId { get; set; }
        public string Email { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
        public DateTime CreationDate { get; set; }
        public IList<int> RolesIds { get; set; }
    }
}