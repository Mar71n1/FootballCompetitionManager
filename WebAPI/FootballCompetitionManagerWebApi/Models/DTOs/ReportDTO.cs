using System;

namespace FootballCompetitionManagerWebApi.Models.DTOs
{
    public class ReportDTO
    {
        public ReportDTO(Report report)
        {
            ReportId = report.ReportId;
            Description = report.Description;
            IsSolved = report.IsSolved;
            UserId = report.User.UserId;
            Username = report.User.Username;
            SentDate = report.SentDate;
            SolvedDate = report.SolvedDate;
        }

        public int ReportId { get; set; }
        public string Description { get; set; }
        public bool IsSolved { get; set; }
        public int UserId { get; set; }
        public string Username { get; set; }
        public DateTime SentDate { get; set; }
        public Nullable<DateTime> SolvedDate { get; set; }
    }
}