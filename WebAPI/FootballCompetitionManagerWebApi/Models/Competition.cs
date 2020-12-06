//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace FootballCompetitionManagerWebApi.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class Competition
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public Competition()
        {
            this.CompetitionTeams = new HashSet<CompetitionTeam>();
            this.LeagueSeasons = new HashSet<LeagueSeason>();
            this.Matches = new HashSet<Match>();
        }
    
        public int CompetitionId { get; set; }
        public string Name { get; set; }
        public int NumberOfTeams { get; set; }
        public int MatchLength { get; set; }
        public int PlayersPerTeam { get; set; }
        public int OwnerId { get; set; }
        public string Description { get; set; }
        public System.DateTime CreationDate { get; set; }
        public int CompetitionStatusId { get; set; }
    
        public virtual CompetitionStatus CompetitionStatus { get; set; }
        public virtual User User { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<CompetitionTeam> CompetitionTeams { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<LeagueSeason> LeagueSeasons { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<Match> Matches { get; set; }
    }
}