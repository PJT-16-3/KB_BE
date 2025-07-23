package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.dto.oauth.KakaoUserInfoDto;
import org.scoula.service.oauth.KakaoOauthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoOauthService kakaoOauthService;

    /**
     * 카카오 로그인 후 리다이렉트되는 callback 엔드포인트
     * @param code 카카오에서 전달하는 인가 코드
     * @return KakaoUserInfoDto + JWT
     */
    @GetMapping("/oauth/kakao/callback")
    @ResponseBody
    public ResponseEntity<KakaoUserInfoDto> kakaoLogin(@RequestParam("code") String code) {
        log.info("카카오 인가 코드 수신: {}", code);

        KakaoUserInfoDto userInfo = kakaoOauthService.processKakaoLogin(code);
        return ResponseEntity.ok(userInfo);
    }
}
