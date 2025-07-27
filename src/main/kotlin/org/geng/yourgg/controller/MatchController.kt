package org.geng.yourgg.controller

import org.geng.yourgg.service.MatchService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Controller
class MatchController(private val matchService: MatchService) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/match")
    fun getMatchDetails(
        @RequestParam("riotId") riotId: String,
        @RequestParam("region") region: String, // region 파라미터 추가
        model: Model
    ): String {
        try {
            val matchFuture = matchService.getLastMatchDetail(riotId, region)
            val matchData = matchFuture.get(10, TimeUnit.SECONDS)
            model.addAttribute("match", matchData)
            return "match-details"
        } catch (e: Exception) {
            val rootCause = (e as? ExecutionException)?.cause ?: e
            val errorMessage = (rootCause as? ResponseStatusException)?.reason
                ?: "알 수 없는 오류가 발생했습니다: ${rootCause.message}"
            model.addAttribute("error", errorMessage)
            return "index"
        }
    }
}