package org.scoula.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.swagger.Email.SwaggerSendVerificationCode;
import org.scoula.dto.swagger.Email.SwaggerVerifyEmailCode;
import org.scoula.service.EmailService;
import org.scoula.service.EmailVerificationService;
import org.scoula.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "Email 인증 API")
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Log4j2
public class EmailController {
    private final EmailService emailService;
    private final EmailVerificationService emailVerification;

    @ApiOperation("회원 가입 시 입력한 이메일로 인증 코드 발송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "검증 코드 발송 완료!")
    })
    @PostMapping("")
    public ResponseEntity<?> sendVerificationCode(@RequestBody SwaggerSendVerificationCode body) {
        String email = body.getUserId() == null ? "" : body.getUserId();
        emailService.sendVerificationCode(email);
        log.info("[/v1/email] 캐시에 저장된 코드: {}", emailService.getCachedCode(email));
        return ResponseEntity.ok(Map.of("message", "검증 코드 발송 완료!"));
    }


    @ApiOperation("이메일 인증 코드 검증")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이메일 인증 성공"),
            @ApiResponse(code = 400, message = "이메일 인증 실패")
    })
    @PostMapping("/verification")
    public ResponseEntity<?> verifyEmailCode(@RequestBody SwaggerVerifyEmailCode body) {
        String email = body.getUserId() == null ? "" : body.getUserId();
        String inputCode = body.getCode() == null ? "" : body.getCode();

        boolean isVerified = emailVerification.verifyCode(email, inputCode);

        if (isVerified) {
            log.info("[verifyEmailCode] 이메일 인증 성공: {}", email);
            return ResponseEntity.ok(Map.of("message", "이메일 인증 성공"));
        } else {
            log.warn("[verifyEmailCode] 이메일 인증 실패: {}", email);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "이메일 인증 실패"));
        }
    }
}
