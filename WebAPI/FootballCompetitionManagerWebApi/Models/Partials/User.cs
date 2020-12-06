using System;

namespace FootballCompetitionManagerWebApi.Models
{
    public partial class User
    {
        public User(UserToRegister userToRegister) : this()
        {
            Email = userToRegister.Email;
            Username = userToRegister.Username;
            Password = userToRegister.Password;
            CreationDate = DateTime.Now;
        }
    }
}