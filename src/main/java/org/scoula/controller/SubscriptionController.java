package org.scoula.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.AptDetailDTO;
import org.scoula.dto.HouseListDTO;
import org.scoula.dto.OfficetelDetailDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.AptService;
import org.scoula.service.HouseService;
import org.scoula.service.OfficetelService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@Api(tags = "청약공고 API", description = "모든 청약공고 조회 및 상세 조회 기능 제공")
@RequestMapping("/v1/subscriptions")
public class SubscriptionController {
    private final HouseService houseService;
    private final AptService aptService;
    private final OfficetelService officetelService;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

    @GetMapping("")
    @ApiOperation(
            value = "모든 청약공고 가져오기",
            notes = "user의 즐겨찾기 정보를 포함한 전체 공고 리스트를 반환합니다. 인증 헤더 필요"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = HouseListDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> getHousingList(@ApiParam(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        try {

            String accessToken = tokenUtils.extractAccessToken(bearerToken);
            String userId = jwtProcessor.getUsername(accessToken);
            int userIdx = userMapper.findUserIdxByUserId(userId);
            List<HouseListDTO> list = houseService.getAllHousingList(userIdx);
            return ResponseEntity.ok(list);

        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("청약 공고 전체 조회 중 예외 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "청약 공고를 불러오는 중 알 수 없는 오류 발생"));
        }
    }


    @GetMapping("/apartments/detail")
    @ApiOperation(value = "아파트 청약공고 상세 정보 조회", notes = "pblanc_no(청약공고번호)로 아파트 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = AptDetailDTO.class),
            @ApiResponse(code = 404, message = "공고를 찾을 수 없음"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> getApartmentDetail(@ApiParam(value = "아파트 공고번호", example = "2025000306", required = true) @RequestParam("pblanc_no") String pblancNo) {
        try {
            aptService.incrementAptViewCount(pblancNo);
            AptDetailDTO detail = aptService.getAptDetail(pblancNo);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("아파트 상세 조회 중 예외 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "아파트 상세 정보를 불러오는 중 오류 발생"));
        }
    }


    @GetMapping("/officetels/detail")
    @ApiOperation(value = "오피스텔 청약공고 상세 정보 조회", notes = "pblanc_no(청약공고번호)로 오피스텔 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = OfficetelDetailDTO.class),
            @ApiResponse(code = 404, message = "공고를 찾을 수 없음"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> getOfficetelDetail(@ApiParam(value = "오피스텔 공고번호", example = "2025950040", required = true)
                                                    @RequestParam("pblanc_no") String pblancNo) {
        try {
            officetelService.incrementOfficeViewCount(pblancNo);
            OfficetelDetailDTO detail = officetelService.getOfficetelDetail(pblancNo);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("오피스텔 상세 조회 중 예외 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "오피스텔 상세 정보를 불러오는 중 오류 발생"));
        }
    }

}
