using System;

namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class TeamDTO
    {
        public TeamDTO()
        {

        }

        public TeamDTO(Team team)
        {
            TeamId = team.TeamId;
            Name = team.Name;
            OwnerId = team.OwnerId;
            OwnerUsername = team.User.Username;
            Description = team.Description;
            CreationDate = team.CreationDate;
        }

        public int TeamId { get; set; }
        public string Name { get; set; }
        public int OwnerId { get; set; }
        public string OwnerUsername { get; set; }
        public string Description { get; set; }
        public DateTime CreationDate { get; set; }
    }
}