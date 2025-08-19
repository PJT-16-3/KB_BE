package org.scoula.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.swagger.Rank.RankAreaResponseDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.RankService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Log4j2
@RequiredArgsConstructor
@Api(tags = "청약 순위 API", description = "아파트 청약 신청자의 순위를 계산합니다.")
@RequestMapping("")
public class RankController {

    private final RankService rankService;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

    @GetMapping("/v1/subscriptions/apartments/detail/rank")
    @ApiOperation(
            value = "아파트 청약 순위 계산",
            notes = "사용자 로그인 정보와 아파트 공고번호를 바탕으로 면적별 청약 순위를 계산하여 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = RankAreaResponseDTO.class),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "해당 공고 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> getApartmentRank(
            @ApiParam(value = "아파트 공고번호", example = "2025000306", required = true)
            @RequestParam("pblanc_no") String pblancNo,
            @ApiParam(hidden = true) HttpServletRequest request) {

        try {
            String bearerToken = request.getHeader("Authorization");
            String accessToken = tokenUtils.extractAccessToken(bearerToken);
            String userId = jwtProcessor.getUsername(accessToken);
            int userIdx = userMapper.findUserIdxByUserId(userId);

            RankAreaResponseDTO rankResult = rankService.calculateApartmentRank(userIdx, pblancNo);

            return ResponseEntity.ok(rankResult);

        } catch (IllegalArgumentException e) {
            log.warn("청약 순위 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(404).body(
                    java.util.Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            log.error("청약 순위 조회 중 예외 발생", e);
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "청약 순위를 계산하는 중 오류가 발생했습니다.")
            );
        }
    }
}
