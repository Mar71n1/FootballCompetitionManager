namespace FootballCompetitionManagerWebApi.Models
{
    public class UserToRegister
    {
        public string Email { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
        public string ConfirmPassword { get; set; }

        public bool ValidatePassword()
        {
            return Password.Equals(ConfirmPassword);
        }
    }
}