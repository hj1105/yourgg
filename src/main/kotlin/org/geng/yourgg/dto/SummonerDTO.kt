package org.geng.yourgg.dto

data class SummonerDTO(
    // 플레이어의 고유 식별자 (Player Universally Unique ID)
    // 매치 V5 API를 호출할 때 핵심 키로 사용됩니다.
    val puuid: String,

    // 소환사의 고유 ID (암호화됨)
    val id: String,

    // 계정의 고유 ID (암호화됨)
    val accountId: String,

    // 소환사 이름 (게임 닉네임)
    val name: String,

    // 소환사 아이콘 ID
    val profileIconId: Int,

    // 소환사 레벨
    val summonerLevel: Long
)