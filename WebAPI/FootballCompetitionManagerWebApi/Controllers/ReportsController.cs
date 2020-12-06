using FootballCompetitionManagerWebApi.Models;
using FootballCompetitionManagerWebApi.Models.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Http;

namespace FootballCompetitionManagerWebApi.Controllers
{
    [RoutePrefix("api/reports")]
    public class ReportsController : ApiController
    {
        private FootballCompetitionManagerDbContext dbContext = new FootballCompetitionManagerDbContext();

        [Route("")]
        [HttpGet]
        public IHttpActionResult GetReports(bool solved)
        {
            IQueryable<Report> reports = dbContext.Reports.Where(report => report.IsSolved == solved);
            return Content(HttpStatusCode.OK, getReportsDto(reports));
        }

        [Route("create")]
        [HttpPost]
        public IHttpActionResult CreateReport(Report report)
        {
            if (report == null)
            {
                return Content(HttpStatusCode.BadRequest, "Report object passed in bad format");
            }

            // Przypisanie użytkownika z zapytania
            string username = HttpContext.Current.User.Identity.Name;
            User user = dbContext.Users.Where(u => u.Username.Equals(username)).FirstOrDefault();
            
            report.UserId = user.UserId;
            report.IsSolved = false;
            report.SentDate = DateTime.Now;

            dbContext.Reports.Add(report);
            dbContext.SaveChanges();

            return Content(HttpStatusCode.Created, new ReportDTO(report));
        }

        [Route("{reportId}/markassolved")]
        [HttpPost]
        public IHttpActionResult MarkAsSolved(int reportId)
        {
            Report report = dbContext.Reports.Find(reportId);
            report.IsSolved = true;
            report.SolvedDate = DateTime.Now;
            dbContext.SaveChanges();
            return Content(HttpStatusCode.OK, new ReportDTO(report));
        }

        private IList<ReportDTO> getReportsDto(IEnumerable<Report> reports)
        {
            IList<ReportDTO> reportsDto = new List<ReportDTO>();
            foreach (Report report in reports)
                reportsDto.Add(new ReportDTO(report));
            return reportsDto;
        }
    }
}