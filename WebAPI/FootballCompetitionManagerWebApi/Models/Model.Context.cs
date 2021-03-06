﻿//------------------------------------------------------------------------------
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
    using System.Data.Entity;
    using System.Data.Entity.Infrastructure;
    
    public partial class FootballCompetitionManagerDbContext : DbContext
    {
        public FootballCompetitionManagerDbContext()
            : base("name=FootballCompetitionManagerDbContext")
        {
        }
    
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            throw new UnintentionalCodeFirstException();
        }
    
        public virtual DbSet<Competition> Competitions { get; set; }
        public virtual DbSet<CompetitionStatus> CompetitionStatuses { get; set; }
        public virtual DbSet<CompetitionTeam> CompetitionTeams { get; set; }
        public virtual DbSet<LeagueSeason> LeagueSeasons { get; set; }
        public virtual DbSet<Match> Matches { get; set; }
        public virtual DbSet<MatchStatus> MatchStatuses { get; set; }
        public virtual DbSet<Report> Reports { get; set; }
        public virtual DbSet<RequestStatus> RequestStatuses { get; set; }
        public virtual DbSet<Role> Roles { get; set; }
        public virtual DbSet<Team> Teams { get; set; }
        public virtual DbSet<TeamUser> TeamUsers { get; set; }
        public virtual DbSet<User> Users { get; set; }
    }
}
