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


    /**
     * 카카오 로그인 페이지로 리다이렉트
     */
//    @GetMapping("/login")
//    @ApiOperation(value = "카카오 로그인 요청", notes = "카카오 인증 페이지로 리다이렉트합니다.")
//    public String redirectToKakao() {
//        String clientId = "53da207a5cc86b7ec03890c960d2937b";
//        String redirectUri = "http://localhost:5173/kakao/callback";
//        return "redirect:https://kauth.kakao.com/oauth/authorize"
//                + "?client_id=" + clientId
//                + "&redirect_uri=" + redirectUri
//                + "&response_type=code";
//    }
}
