package org.geng.yourgg.dto

data class MatchDetailViewDTO(
    val gameMode: String,
    val gameDuration: Long,
    val teams: List<TeamViewDTO>
)

data class TeamViewDTO(
    val teamId: Int,
    val win: Boolean,
    val participants: List<ParticipantDTO>
)