package org.geng.yourgg.dto

data class MatchDTO(
    val metadata: MetadataDTO,
    val info: InfoDTO
)

data class MetadataDTO(
    val matchId: String,
    val participants: List<String> // 참여한 플레이어들의 PUUID 리스트
)

data class InfoDTO(
    val gameCreation: Long,
    val gameDuration: Long,    // 게임 시간 (초)
    val gameMode: String,      // 게임 모드 (예: CLASSIC, ARAM)
    val gameType: String,      // 게임 타입 (예: MATCHED_GAME)
    val participants: List<ParticipantDTO>, // 인게임 플레이어 10명의 상세 정보
    val teams: List<TeamDTO>   // 블루팀, 레드팀 정보
)

data class ParticipantDTO(
    val summonerName: String, // 소환사 이름
    val puuid: String,        // 플레이어 PUUID
    val participantId: Int,   // 참가자 ID (1-10)
    val teamId: Int,          // 팀 ID (100: 블루, 200: 레드)

    // 챔피언 정보
    val championName: String,
    val champLevel: Int,

    // KDA
    val kills: Int,
    val deaths: Int,
    val assists: Int,

    // 아이템
    val item0: Int,
    val item1: Int,
    val item2: Int,
    val item3: Int,
    val item4: Int,
    val item5: Int,
    val item6: Int, // 장신구

    // 골드 및 CS
    val goldEarned: Int,
    val totalMinionsKilled: Int,

    // 승리 여부
    val win: Boolean,

    val riotIdGameName: String
)

data class TeamDTO(
    val teamId: Int,
    val win: Boolean, // 팀의 승리 여부
    val objectives: ObjectivesDTO // 팀이 달성한 오브젝트 정보
)

data class ObjectivesDTO(
    val champion: ObjectiveDTO,
    val baron: ObjectiveDTO,
    val dragon: ObjectiveDTO,
    val inhibitor: ObjectiveDTO,
    val riftHerald: ObjectiveDTO,
    val tower: ObjectiveDTO
)

data class ObjectiveDTO(
    val first: Boolean, // 첫 킬 여부
    val kills: Int      // 총 킬 수
)