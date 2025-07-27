package org.geng.yourgg.retrofit

import org.geng.yourgg.dto.AccountDTO
import org.geng.yourgg.dto.MatchDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RiotApiService {
    @GET("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    fun getAccountByRiotId(
        @Path("gameName") gameName: String,
        @Path("tagLine") tagLine: String
    ): Call<AccountDTO>

    @GET("/lol/match/v5/matches/by-puuid/{puuid}/ids")
    fun getMatchIdsByPuuid(
        @Path("puuid") puuid: String,
        @Query("queue") queue: Int,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 1
    ): Call<List<String>>

    @GET("/lol/match/v5/matches/{matchId}")
    fun getMatchDetail(@Path("matchId") matchId: String): Call<MatchDTO>
}