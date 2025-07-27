package org.geng.yourgg.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.geng.yourgg.config.RegionRouting
import org.geng.yourgg.dto.*
import org.geng.yourgg.retrofit.RiotApiService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.server.ResponseStatusException
import retrofit2.Call
import retrofit2.Response

@ExtendWith(MockKExtension::class)
class MatchServiceTest {

    @MockK
    private lateinit var regionalApiServices: Map<String, RiotApiService>

    @InjectMockKs
    private lateinit var matchService: MatchService

    private val mockApiService = mockk<RiotApiService>()

    @Test
    fun `성공 - KR 지역으로 마지막 매치 정보를 정상적으로 가져온다`() {
        // given
        val riotId = "test#kr1"
        val region = "kr"
        val regionalHost = RegionRouting.getRegionalHost(region) // "asia"
        val puuid = "test-puuid"
        val matchId = "KR_12345"

        val mockAccountDto = AccountDTO(puuid, "test", "kr1")
        val mockMatchDto = createMockMatchDTO()

        // given
        every { regionalApiServices[regionalHost] } returns mockApiService
        every { mockApiService.getAccountByRiotId("test", "kr1") } returns mockSuccessCall(mockAccountDto)
        every { mockApiService.getMatchIdsByPuuid(puuid, 420, 0, 1) } returns mockSuccessCall(listOf(matchId))
        every { mockApiService.getMatchDetail(matchId) } returns mockSuccessCall(mockMatchDto)

        // when
        val resultFuture = matchService.getLastMatchDetail(riotId, region)
        val result = resultFuture.get()

        // then
        assertNotNull(result)
        assertEquals("CLASSIC", result.gameMode)
    }

    @Test
    fun `성공 - NA 지역으로 마지막 매치 정보를 정상적으로 가져온다`() {
        // given
        val riotId = "test#na1"
        val region = "na1"
        val regionalHost = RegionRouting.getRegionalHost(region) // "americas"
        val puuid = "test-puuid-na"
        val matchId = "NA1_67890"

        val mockAccountDto = AccountDTO(puuid, "test", "na1")
        val mockMatchDto = createMockMatchDTO()

        // given
        every { regionalApiServices[regionalHost] } returns mockApiService
        every { mockApiService.getAccountByRiotId("test", "na1") } returns mockSuccessCall(mockAccountDto)
        every { mockApiService.getMatchIdsByPuuid(puuid, 420, 0, 1) } returns mockSuccessCall(listOf(matchId))
        every { mockApiService.getMatchDetail(matchId) } returns mockSuccessCall(mockMatchDto)

        // when
        val result = matchService.getLastMatchDetail(riotId, region).get()

        // then
        assertNotNull(result)
    }

    @Test
    fun `실패 - 존재하지 않는 Riot ID로 조회 시 예외를 던진다`() {
        // given
        val riotId = "없는아이디#kr1"
        val region = "kr"
        val regionalHost = RegionRouting.getRegionalHost(region)

        // given
        every { regionalApiServices[regionalHost] } returns mockApiService
        every { mockApiService.getAccountByRiotId("없는아이디", "kr1") } returns mockErrorCall(404)

        // when & then
        val exception = assertThrows<ResponseStatusException> {
            matchService.getLastMatchDetail(riotId, region).get()
        }
        assertEquals(404, exception.statusCode.value())
        assertEquals("Riot ID를 해당 지역에서 찾을 수 없습니다.", exception.reason)
    }

    @Test
    fun `실패 - 지원하지 않는 지역으로 조회 시 예외를 던진다`() {
        // given
        val riotId = "test#xx1"
        val region = "xx1" // RegionRouting에 없는 지역

        // when & then
        // RegionRouting 객체가 던지는 예외를 서비스가 그대로 전파하는지 확인
        assertThrows<IllegalArgumentException> {
            matchService.getLastMatchDetail(riotId, region).get()
        }
    }

    private fun <T> mockSuccessCall(data: T): Call<T> {
        val call = mockk<Call<T>>()
        every { call.execute() } returns Response.success(data)
        return call
    }

    private fun <T> mockErrorCall(code: Int): Call<T> {
        val call = mockk<Call<T>>()
        val response = mockk<Response<T>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns code
        every { call.execute() } returns response
        return call
    }

    private fun createMockMatchDTO(): MatchDTO {
        val blueParticipant = ParticipantDTO(
            summonerName = "bluePlayer",
            riotIdGameName = "BluePlayerRiotName", // riotIdGameName 추가
            puuid = "puuid1",
            participantId = 1,
            teamId = 100,
            championName = "Garen",
            champLevel = 18,
            kills = 10,
            deaths = 5,
            assists = 10,
            item0 = 1, item1 = 2, item2 = 3, item3 = 4, item4 = 5, item5 = 6, item6 = 7,
            goldEarned = 15000,
            totalMinionsKilled = 200,
            win = true
        )
        val redParticipant = ParticipantDTO(
            summonerName = "redPlayer",
            riotIdGameName = "RedPlayerRiotName", // riotIdGameName 추가
            puuid = "puuid2",
            participantId = 6,
            teamId = 200,
            championName = "Darius",
            champLevel = 17,
            kills = 5,
            deaths = 10,
            assists = 5,
            item0 = 1, item1 = 2, item2 = 3, item3 = 4, item4 = 5, item5 = 6, item6 = 7,
            goldEarned = 14000,
            totalMinionsKilled = 180,
            win = false
        )

        val blueTeam = TeamDTO(100, true, mockk())
        val redTeam = TeamDTO(200, false, mockk())

        val info = InfoDTO(
            gameCreation = 123456789L,
            gameDuration = 1800,
            gameMode = "CLASSIC",
            gameType = "MATCHED_GAME",
            participants = listOf(blueParticipant, redParticipant),
            teams = listOf(blueTeam, redTeam)
        )
        val metadata = MetadataDTO("KR_12345", listOf("puuid1", "puuid2"))

        return MatchDTO(metadata, info)
    }
}