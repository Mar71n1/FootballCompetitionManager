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
    
    public partial class CompetitionTeam
    {
        public int CompetitionId { get; set; }
        public int TeamId { get; set; }
        public int RequestStatusId { get; set; }
    
        public virtual Competition Competition { get; set; }
        public virtual RequestStatus RequestStatus { get; set; }
        public virtual Team Team { get; set; }
    }
}