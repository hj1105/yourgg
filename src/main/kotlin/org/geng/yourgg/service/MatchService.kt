package org.geng.yourgg.service

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.geng.yourgg.config.RegionRouting
import org.geng.yourgg.dto.MatchDTO
import org.geng.yourgg.dto.MatchDetailViewDTO
import org.geng.yourgg.dto.TeamViewDTO
import org.geng.yourgg.retrofit.RiotApiService
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.CompletableFuture

@Service
class MatchService(
    private val regionalApiServices: Map<String, RiotApiService> // Map으로 주입받음
) {

    private fun parseRiotId(riotId: String): Pair<String, String> {
        val parts = riotId.split("#")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Riot ID 형식이 올바르지 않습니다. (예: 게임이름#태그)")
        }
        return Pair(parts[0], parts[1])
    }

    @Async
    // 캐시 키에 region을 추가하여 지역별로 캐싱
    @Cacheable("matchCache", key = "#riotId.toLowerCase() + '-' + #region")
    @RateLimiter(name = "riotApiLimiter")
    fun getLastMatchDetail(riotId: String, region: String): CompletableFuture<MatchDetailViewDTO> {
        val (gameName, tagLine) = parseRiotId(riotId)

        // 1. 지역에 맞는 API 서비스 동적 선택
        val regionalHost = RegionRouting.getRegionalHost(region)
        val apiService = regionalApiServices[regionalHost]
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 지역입니다: $region")

        // 2. 선택된 서비스로 PUUID 조회
        val accountCall = apiService.getAccountByRiotId(gameName, tagLine)
        val accountResponse = accountCall.execute()
        if (!accountResponse.isSuccessful) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Riot ID를 해당 지역에서 찾을 수 없습니다.")
        }
        val puuid = accountResponse.body()!!.puuid

        // 3. 선택된 서비스로 최근 매치 ID 조회
        val matchId = findLastSummonersRiftMatchId(puuid, apiService)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "최근 소환사의 협곡 매치를 찾을 수 없습니다.")

        // 4. 선택된 서비스로 매치 상세 정보 조회
        val matchCall = apiService.getMatchDetail(matchId)
        val matchResponse = matchCall.execute()
        if (!matchResponse.isSuccessful) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "매치 정보를 가져오는 데 실패했습니다.")
        }

        val matchDTO = matchResponse.body()!!
        val viewDTO = transformToViewDTO(matchDTO)

        return CompletableFuture.completedFuture(viewDTO)
    }

    private fun findLastSummonersRiftMatchId(puuid: String, apiService: RiotApiService): String? {
        for (queueId in listOf(420, 440, 400)) {
            val response = apiService.getMatchIdsByPuuid(puuid, queue = queueId, count = 1).execute()
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                return response.body()!!.first()
            }
        }
        return null
    }

    private fun transformToViewDTO(matchDTO: MatchDTO): MatchDetailViewDTO {
        val participantsByTeam = matchDTO.info.participants.groupBy { it.teamId }
        val teamViews = matchDTO.info.teams.map { team ->
            TeamViewDTO(
                teamId = team.teamId,
                win = team.win,
                participants = participantsByTeam[team.teamId] ?: emptyList()
            )
        }
        return MatchDetailViewDTO(
            gameMode = matchDTO.info.gameMode,
            gameDuration = matchDTO.info.gameDuration,
            teams = teamViews
        )
    }
}