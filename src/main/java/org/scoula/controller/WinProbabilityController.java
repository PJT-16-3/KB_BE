package org.scoula.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.scoula.dto.PythonAptRequestDTO;
import org.scoula.dto.PythonOfficetelRequestDTO;
import org.scoula.dto.swagger.WinProbability.SwaggerWinProbabilityDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.PythonApiService;
import org.scoula.util.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/predict/probability")
@Api(tags = "당첨 확률 예측 API", description = "청약 당첨 확률 예측 관련 API")
public class WinProbabilityController {

    private final PythonApiService pythonApiService;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

    @ApiOperation(
            value = "아파트 당첨 확률 예측",
            notes = "회원의 청약 신청 조건과 해당 공고 정보를 기반으로 당첨 확률을 예측합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "예측 성공", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"probability\": 87.87 }")
            })),
            @ApiResponse(code = 400, message = "요청 오류 (필수 값 누락 또는 잘못된 입력)", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"error\": \"pblanc_no가 없습니다.\" }")
            })),
            @ApiResponse(code = 422, message = "유효성 검증 실패", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"error\": \"입력값을 다시 확인해 주세요.\", \"fields\": { \"house_rank\": \"house_rank오류.\" } }")
            }))
    })
    @PostMapping("/apt")
    public ResponseEntity<?> getPrediction(
            @ApiParam(hidden = true)
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody SwaggerWinProbabilityDTO body
    ) {
        String accessToken = tokenUtils.extractAccessToken(bearerToken);
        String userId = jwtProcessor.getUsername(accessToken);
        int userIdx = userMapper.findUserIdxByUserId(userId);

        if (body.getPblancNo() == null || body.getPblancNo().isBlank()) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", "pblanc_no가 없습니다.")
            );
        }

        PythonAptRequestDTO requestDTO = pythonApiService.buildPythonAptRequest(userIdx, body.getPblancNo().trim());
        if (requestDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "당첨확률을 알 수 없습니다."));
        }

        Map<String, Object> result = pythonApiService.requestPrediction(requestDTO);
        return ResponseEntity.ok(result);
    }


    @ApiOperation(
            value = "아파트 당첨 확률 예측",
            notes = "회원의 청약 신청 조건과 해당 공고 정보를 기반으로 당첨 확률을 예측합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "예측 성공", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"probability\": 87.87 }")
            })),
            @ApiResponse(code = 400, message = "요청 오류 (필수 값 누락 또는 잘못된 입력)", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"error\": \"pblanc_no가 없습니다.\" }")
            })),
            @ApiResponse(code = 422, message = "유효성 검증 실패", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{ \"error\": \"입력값을 다시 확인해 주세요.\", \"fields\": { \"house_rank\": \"house_rank오류.\" } }")
            }))
    })
    @PostMapping("/officetel")
    public ResponseEntity<?> getOfficetelPrediction(
            @ApiParam(hidden = true)
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody SwaggerWinProbabilityDTO body
    ) {
        String accessToken = tokenUtils.extractAccessToken(bearerToken);
        String userId = jwtProcessor.getUsername(accessToken);
        int userIdx = userMapper.findUserIdxByUserId(userId);

        if (body.getPblancNo() == null || body.getPblancNo().isBlank()) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", "pblanc_no가 없습니다.")
            );
        }

        PythonOfficetelRequestDTO requestDTO = pythonApiService.buildPythonOfficetelRequest(userIdx, body.getPblancNo().trim());
        if (requestDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "당첨확률을 알 수 없습니다."));
        }

        Map<String, Object> result = pythonApiService.requestOfficetelPrediction(requestDTO);
        return ResponseEntity.ok(result);
    }
}
