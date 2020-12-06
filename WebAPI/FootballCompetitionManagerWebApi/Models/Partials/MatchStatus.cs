using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FootballCompetitionManagerWebApi.Models
{
    public partial class MatchStatus
    {
		public const int PROPOSED_HOME_TEAM_OWNER = 1;
		public const int PROPOSED_AWAY_TEAM_OWNER = 2;
		public const int PLANNED = 3;
		public const int SCORE_PROPOSED_HOME_TEAM_OWNER = 4;
		public const int SCORE_PROPOSED_AWAY_TEAM_OWNER = 5;
		public const int SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER = 6;
		public const int SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER = 7;
		public const int SCORE_ACCEPTED = 8;
		public const int SCORE_UNKNOWN = 9;
    }
}