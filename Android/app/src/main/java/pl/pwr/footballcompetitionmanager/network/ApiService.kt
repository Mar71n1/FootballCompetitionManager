package pl.pwr.footballcompetitionmanager.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.pwr.footballcompetitionmanager.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://footballcompetitionmanagerwebapi.azurewebsites.net/api/"

val logging: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder().addInterceptor(logging).build())
    .build()

interface ApiService {

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Competitions
    @GET("competitions/{competitionId}")
    suspend fun getCompetition(@Header("Authorization") credentials: String,
                               @Path("competitionId") competitionId: Int): Competition

    @GET("competitions")
    suspend fun getCompetitions(@Header("Authorization") credentials: String): List<Competition>

    @GET("competitions/foruser")
    suspend fun getCompetitionsForUser(@Header("Authorization") credentials: String,
                                       @Query("userId") userId: Int): List<Competition>

    @GET("competitions/byteam")
    suspend fun getCompetitionsByTeam(@Header("Authorization") credentials: String,
                                      @Query("teamId") teamId: Int): List<Competition>

    @GET("competitions/search")
    suspend fun searchCompetitionsByName(@Header("Authorization") credentials: String,
                                         @Query("name") name: String): List<Competition>

    @POST("competitions/{competitionId}/start")
    suspend fun startCompetition(@Header("Authorization") credentials: String,
                                 @Path("competitionId") competitionId: Int): Response<Void>

    @POST("competitions/{competitionId}/finish")
    suspend fun finishCompetition(@Header("Authorization") credentials: String,
                                  @Path("competitionId") competitionId: Int): Response<Void>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CompetitionTeamsController
    @POST("competitionteams/sendrequesttojoin")
    suspend fun sendRequestToJoinCompetition(@Header("Authorization") credentials: String,
                                             @Query("competitionId") competitionId: Int,
                                             @Query("teamId") teamId: Int): Response<Unit>

    @DELETE("competitionteams/cancelrequest")
    suspend fun cancelTeamRequest(@Header("Authorization") credentials: String,
                                  @Query("competitionId") competitionId: Int,
                                  @Query("teamId") teamId: Int): Response<Unit>

    @DELETE("competitionteams/remove")
    suspend fun removeTeamFromCompetition(@Header("Authorization") credentials: String,
                                          @Query("competitionId") competitionId: Int,
                                          @Query("teamId") teamId: Int): Response<Unit>

    @POST("competitionteams/acceptrequest")
    suspend fun acceptTeamRequest(@Header("Authorization") credentials: String,
                                  @Query("competitionId") competitionId: Int,
                                  @Query("teamId") teamId: Int): Response<Unit>

    @POST("competitionteams/rejectrequest")
    suspend fun rejectTeamRequest(@Header("Authorization") credentials: String,
                                  @Query("competitionId") competitionId: Int,
                                  @Query("teamId") teamId: Int): Response<Unit>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // LeagueSeasons
    @GET("leagueseasons/{leagueseasonid}")
    suspend fun getLeagueSeason(@Header("Authorization") credentials: String,
                                @Path("leagueseasonid") leagueSeasonId: Int): LeagueSeason

    @GET("leagueseasons")
    suspend fun getLeagueSeasonByCompetitionId(@Header("Authorization") credentials: String,
                                               @Query("competitionid") competitionId: Int): LeagueSeason

    @POST("leagueseasons/create")
    suspend fun createLeagueSeason(@Header("Authorization") credentials: String,
                                   @Body leagueSeason: LeagueSeason): LeagueSeason

    @PUT("leagueseasons/{leagueseasonid}/update")
    suspend fun updateLeagueSeason(@Header("Authorization") credentials: String,
                                   @Path("leagueseasonid") leagueSeasonId: Int,
                                   @Body leagueSeason: LeagueSeason): LeagueSeason

    @DELETE("leagueseasons/{leagueseasonid}/delete")
    suspend fun deleteLeagueSeason(@Header("Authorization") credentials: String,
                                   @Path("leagueseasonid") leagueSeasonId: Int): Response<Void>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Matches
    @GET("matches/{matchId}")
    suspend fun getMatch(@Header("Authorization") credentials: String,
                         @Path("matchId") matchId: Int): Match

    @GET("matches")
    suspend fun getMatches(@Header("Authorization") credentials: String): List<Match>

    @GET("matches/foruser")
    suspend fun getMatchesForUser(@Header("Authorization") credentials: String,
                                  @Query("userId") userId: Int): List<Match>

    @GET("matches/incomingbyteam")
    suspend fun getIncomingMatchesByTeam(@Header("Authorization") credentials: String,
                                         @Query("teamId") teamId: Int): List<Match>

    @GET("matches/latestresultsbyteam")
    suspend fun getLatestResultsByTeam(@Header("Authorization") credentials: String,
                                       @Query("teamId") teamId: Int): List<Match>

    @GET("matches")
    suspend fun getCompetitionMatches(@Header("Authorization") credentials: String,
                                      @Query("competitionId") competitionId: Int): List<Match>

    @GET("matches/results")
    suspend fun getCompetitionResults(@Header("Authorization") credentials: String,
                                      @Query("competitionId") competitionId: Int): List<Match>

    @POST("matches/create")
    suspend fun createMatch(@Header("Authorization") credentials: String,
                            @Body match: Match): Match

    @DELETE("matches/{matchId}")
    suspend fun deleteMatch(@Header("Authorization") credentials: String,
                            @Path("matchId") matchId: Int): Response<Void>

    @PUT("matches/{matchId}")
    suspend fun updateMatch(@Header("Authorization") credentials: String,
                            @Path("matchId") matchId: Int,
                            @Body match: Match): Match

    @PUT("matches/{matchId}/setscore")
    suspend fun setMatchScore(@Header("Authorization") credentials: String,
                              @Path("matchId") matchId: Int,
                              @Query("homeTeamGoals") homeTeamGoals: Int,
                              @Query("awayTeamGoals") awayTeamGoals: Int): Match

    @PUT("matches/{matchId}/acceptscore")
    suspend fun acceptMatchScore(@Header("Authorization") credentials: String,
                                 @Path("matchId") matchId: Int): Match

    @PUT("matches/{matchId}/rejectscore")
    suspend fun rejectMatchScore(@Header("Authorization") credentials: String,
                                 @Path("matchId") matchId: Int): Match
    @PUT("matches/{matchId}/accept")
    suspend fun acceptMatchProposal(@Header("Authorization") credentials: String,
                                    @Path("matchId") matchId: Int): Match

    @DELETE("matches/{matchId}/reject")
    suspend fun rejectMatchProposal(@Header("Authorization") credentials: String,
                                    @Path("matchId") matchId: Int): Response<Void>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Reports
    @GET("reports")
    suspend fun getReports(@Header("Authorization") credentials: String,
                           @Query("solved") solved: Boolean): List<Report>

    @POST("reports/create")
    suspend fun createReport(@Header("Authorization") credentials: String,
                             @Body report: Report): Report

    @POST("reports/{reportId}/markassolved")
    suspend fun markReportAsSolved(@Header("Authorization") credentials: String,
                                   @Path("reportId") reportId: Int): Report
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Teams
    @GET("teams/{teamId}")
    suspend fun getTeam(@Header("Authorization") credentials: String,
                        @Path("teamId") teamId: Int): Team

    @GET("teams")
    suspend fun getTeams(@Header("Authorization") credentials: String): List<Team>

    @PUT("teams/{teamid}/update")
    suspend fun updateTeam(@Header("Authorization") credentials: String,
                           @Path("teamid") teamId: Int,
                           @Body team: Team): Team

    @DELETE("teams/{teamId}/delete")
    suspend fun deleteTeam(@Header("Authorization") credentials: String,
                           @Path("teamId") teamId: Int): Response<Unit>

    @GET("teams/foruser")
    suspend fun getTeamsForUser(@Header("Authorization") credentials: String,
                                @Query("userId") userId: Int): List<Team>

    @GET("teams")
    suspend fun getOwnerTeams(@Header("Authorization") credentials: String,
                              @Query("ownerId") ownerId: Int): List<Team>

    @GET("teams")
    suspend fun getCompetitionTeams(@Header("Authorization") credentials: String,
                                    @Query("competitionId") competitionId: Int): List<Team>

    @GET("teams/search")
    suspend fun searchTeamsByName(@Header("Authorization") credentials: String,
                                  @Query("name") name: String): List<Team>

    @GET("teams/pendingrequeststeamsforcompetition")
    suspend fun getPendingRequestsTeamsForCompetition(@Header("Authorization") credentials: String,
                                                      @Query("competitionid") competitionId: Int): List<Team>

    @POST("teams/create")
    suspend fun createTeam(@Header("Authorization") credentials: String,
                           @Body team: Team): Team

    @POST("teams/{teamId}/sendrequesttojoin")
    suspend fun sendRequestToJoinTeam(@Header("Authorization") credentials: String,
                                      @Path("teamId") teamId: Int): Boolean
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // TeamUsersController
    @GET("teamusers/status")
    suspend fun getActualRequestStatus(@Header("Authorization") credentials: String,
                                       @Query("teamId") teamId: Int,
                                       @Query("userId") userId: Int): Int

    @DELETE("teamusers/cancelrequest")
    suspend fun cancelUserRequest(@Header("Authorization") credentials: String,
                                  @Query("teamId") teamId: Int,
                                  @Query("userId") userId: Int): Response<Unit>

    @DELETE("teamusers/leaveteam")
    suspend fun removeUserFromTeam(@Header("Authorization") credentials: String,
                                   @Query("teamId") teamId: Int,
                                   @Query("userId") userId: Int): Response<Unit>

    @POST("teamusers/acceptuserrequest")
    suspend fun acceptUserRequest(@Header("Authorization") credentials: String,
                                  @Query("teamId") teamId: Int,
                                  @Query("userId") userId: Int): Response<Unit>

    @POST("teamusers/rejectuserrequest")
    suspend fun rejectUserRequest(@Header("Authorization") credentials: String,
                                  @Query("teamId") teamId: Int,
                                  @Query("userId") userId: Int): Response<Unit>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Users
    //    @FormUrlEncoded
//    @POST("/api/users/register")
//    suspend fun register(@Field("Email") email: String,
//                         @Field("Username") username: String,
//                         @Field("Password") password: String,
//                         @Field("ConfirmPassword") confirmPassword: String /* @Body userToRegister: UserToRegister*/): User

    @FormUrlEncoded
    @POST("users/register")
    suspend fun register(@Field("Email") email: String,
                         @Field("Username") username: String,
                         @Field("Password") password: String,
                         @Field("ConfirmPassword") confirmPassword: String): User

    @GET("users/login")
    suspend fun login(@Header("Authorization") credentials: String): User

    @PUT("users/{userId}")
    suspend fun changeData(@Header("Authorization") credentials: String,
                           @Path("userId") userId: Int,
                           @Body user: User): User

    @GET("users/byteam")
    suspend fun getUsersByTeam(@Header("Authorization") credentials: String,
                               @Query("teamId") teamId: Int): List<User>

    @GET("users/pendingrequestsusersforteam")
    suspend fun getPendingRequestsUsersForTeam(@Header("Authorization") credentials: String,
                                               @Query("teamId") teamId: Int): List<User>
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}