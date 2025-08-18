package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.scoula.dto.oauth.KakaoUserInfoDto;
import org.scoula.security.dto.AuthResultDTO;
import org.scoula.service.oauth.KakaoOauthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/kakao")
@Api(tags = "카카오 API", description = "카카오 소셜 로그인 관련 API")
public class KakaoLoginController {

    private final KakaoOauthService kakaoOauthService;

    @PostMapping("/callback")
    public ResponseEntity<AuthResultDTO> kakaoLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        log.info("카카오 인가 코드 수신: {}", code);

        AuthResultDTO userInfo = kakaoOauthService.processKakaoLogin(code);
        return ResponseEntity.ok(userInfo);
    }
}