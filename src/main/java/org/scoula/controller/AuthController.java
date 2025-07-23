package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Delete;
import org.scoula.security.account.mapper.UserDetailsMapper;
import org.scoula.security.dto.RefreshTokenDTO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final JwtProcessor jwtProcessor;
    private final UserDetailsMapper userDetailsMapper;
    public static final String BEARER_PREFIX = "Bearer ";


    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            //  1. access 토큰 꺼내기
            String accessToken = null;
            if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
                accessToken = bearerToken.substring(BEARER_PREFIX.length());
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "Authorization 헤더가 유효하지 않습니다"));
            }

            // 2. 유효성 검사하기
            if (!jwtProcessor.validateToken(accessToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 Access Token"));
            }

            // 3. Refresh Token 삭제
            String username = jwtProcessor.getUsername(accessToken);
            userDetailsMapper.clearRefreshToken(username);
            log.info("Refresh Token 삭제 완료 - 사용자: {}", username);

            return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류", e);
            return ResponseEntity.status(500).body(Map.of("error", "로그아웃 처리 중 오류 발생"));
        }
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String,String> body) {
        String refresh_token = body.get("refreshToken");

        try{

            // 1. refresh token 유효성 검사 (만기여부)
            if (!jwtProcessor.validateToken(refresh_token)) {
                return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 Refresh Token 입니다"));
            }

            //2.userid 가 필요
            String user_id = jwtProcessor.getUsername(refresh_token);

            //3.db에 있는 refresh 값과 비교 이걸 하기 위해서 userid 가 필요하네
            String savedRefreshToken = userDetailsMapper.getRefreshToken(user_id);
            //비교해보자
            if(savedRefreshToken == null || !savedRefreshToken.equals(refresh_token)) {
                return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 Refresh Token 입니다"));
            }

            //4.일치하면 새로운 access toekn 과 refresh token 발금(rotate방식)
            String newAccessToken = jwtProcessor.generateAccessToken(user_id);
            String newRefreshToken = jwtProcessor.generateRefreshToken(user_id);

            //5.DB에 RefreshToken 저장
            RefreshTokenDTO refreshTokenDTO =  new RefreshTokenDTO();
            refreshTokenDTO.setUser_id(user_id);
            refreshTokenDTO.setJwt_refresh_token(newRefreshToken);
            userDetailsMapper.updateRefreshToken(refreshTokenDTO);

            //6.마지막으로 응답 처리
            return ResponseEntity.ok(Map.of(
                    "access_token", newAccessToken,
                    "refresh_token", newRefreshToken
            ));
        }catch (Exception e) {
            log.error("Refresh Token 처리 중 오류", e);
            return ResponseEntity.status(500).body(Map.of("error", "Refresh Token 처리 중 오류"));
        }

        }

}
