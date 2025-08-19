package org.scoula.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.AllHouseListDTO;
import org.scoula.dto.AptDetailDTO;
import org.scoula.dto.GetRecentChecksDTO;
import org.scoula.dto.RecentCheckDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.AptService;
import org.scoula.service.RecentCheckService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/recentview")
@Log4j2
@RequiredArgsConstructor
@Api(tags = "최근본 공고 조회", description = "유저별로 최근 본 공고 조회")
public class RecentViewController {

    private final RecentCheckService recentCheckService;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;
    private final AptService aptService;
    @GetMapping
    @ApiOperation(value = "최근 본 공고 리스트 조회", notes = "JWT 토큰 기반으로 해당 유저가 최근 본 공고를 조회합니다.")
    public ResponseEntity<?> getRecentViews(
            @RequestHeader("Authorization") String bearerToken
    ) {
        try {
            String accessToken = tokenUtils.extractAccessToken(bearerToken);
            String userId = jwtProcessor.getUsername(accessToken);
            int userIdx = userMapper.findUserIdxByUserId(userId);

            List<AllHouseListDTO> recentList = recentCheckService.getRecentChecks(userIdx);

            return ResponseEntity.ok(recentList);

        } catch (Exception e) {
            log.error("최근 본 공고 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "최근 본 공고 조회 실패"));
        }
    }

}
