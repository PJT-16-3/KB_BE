package org.scoula.controller;


import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.FavoriteRequestDTO;
import org.scoula.dto.HouseListDTO;
import org.scoula.dto.swagger.UserFavorite.SwaggerAddFavoriteRequestDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.UserFavoriteService;
import org.scoula.service.UserService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/v1/me/favorite")
@RequiredArgsConstructor
@Api(tags = "즐겨찾기 API", description = "사용자의 주택 즐겨찾기 추가, 삭제, 조회 기능 제공")
public class UserFavoriteController {
    private final UserMapper userMapper;
    private final UserFavoriteService userFavoriteService;
    private final JwtProcessor jwtProcessor;
    private final TokenUtils tokenUtils;
    private final UserService userService;
    public static final String BEARER_PREFIX = "Bearer ";

    // 즐겨찾기 추가
    @PostMapping(produces = "application/json; charset=UTF-8")
    @ApiOperation(value = "즐겨찾기 추가", notes = "사용자의 즐겨찾기에 주택을 추가합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "즐겨찾기 추가 완료"),
            @ApiResponse(code = 400, message = "이미 즐겨찾기에 존재함")
    })
    public ResponseEntity<?> addFavorite(@ApiParam(hidden = true) @RequestHeader ("Authorization") String bearerToken , @RequestBody SwaggerAddFavoriteRequestDTO body) {
        String accessToken = tokenUtils.extractAccessToken(bearerToken);
        String username = jwtProcessor.getUsername(accessToken);
        int usersIdx = userMapper.findUserIdxByUserId(username);

        FavoriteRequestDTO favorite = FavoriteRequestDTO.builder()
                        .usersIdx(usersIdx)
                        .houseType(body.getHouseType())
                        .pblancNo(body.getPblancNo())
                        .build();

        boolean success = userFavoriteService.addFavorite(favorite.getUsersIdx(), favorite.getHouseType(), favorite.getPblancNo());
        return success ? ResponseEntity.ok("즐겨찾기 추가 완료") :
                ResponseEntity.badRequest().body("이미 즐겨찾기에 존재합니다.");
    }

    // 즐겨찾기 삭제
    @DeleteMapping(produces = "application/json; charset=UTF-8")
    @ApiOperation(value = "즐겨찾기 삭제", notes = "사용자의 즐겨찾기에서 특정 주택을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "즐겨찾기 삭제 완료"),
            @ApiResponse(code = 400, message = "즐겨찾기가 존재하지 않거나 삭제 실패"),
            @ApiResponse(code = 500, message = "서버 오류 발생")
    })
    public ResponseEntity<String> removeFavorite(
            @ApiParam(hidden = true)
            @RequestHeader("Authorization") String bearerToken,

            @ApiParam(value = "주택 유형 (APT / 신혼희망타운 / 오피스텔 등)",example ="APT", required = true)
            @RequestParam("house_type") String houseType,

            @ApiParam(value = "공고 번호", example = "2025000306", required = true)
            @RequestParam("pblanc_no") String pblancNo
    ) {
        try {
            String accessToken = tokenUtils.extractAccessToken(bearerToken);
            String username = jwtProcessor.getUsername(accessToken);
            int usersIdx = userMapper.findUserIdxByUserId(username);

            boolean success = userFavoriteService.deleteFavorite(usersIdx, houseType, pblancNo);

            return success ? ResponseEntity.ok("즐겨찾기 삭제 완료") : ResponseEntity.badRequest().body("즐겨찾기가 존재하지 않거나 삭제 실패");
        } catch (Exception e) {
            log.error("즐겨찾기 삭제 중 오류", e);
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // 특정 사용자의 즐겨찾기 목록
    @GetMapping("/list")
    @ApiOperation(value = "즐겨찾기 목록 조회", notes = "현재 로그인한 사용자의 즐겨찾기 주택 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "즐겨찾기 목록 조회 성공", response = HouseListDTO.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "즐겨찾기 목록 조회 중 오류")
    })
    public ResponseEntity<?> getFavorites(@ApiParam(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        try {
            String accessToken = tokenUtils.extractAccessToken(bearerToken);
            String username = jwtProcessor.getUsername(accessToken);
            int usersIdx = userMapper.findUserIdxByUserId(username);

            List<HouseListDTO> favorites = userFavoriteService.getFavoriteHouses(usersIdx);
            return ResponseEntity.ok(favorites);

        } catch (Exception e) {
            log.error("즐겨찾기 목록 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "즐겨찾기 목록 조회 중 오류"));
        }
    }



}
