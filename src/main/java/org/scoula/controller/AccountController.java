package org.scoula.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.AccountConnectDTO;
import org.scoula.dto.ChungyakAccountDTO;
import org.scoula.exception.NoAccountException;
import org.scoula.service.CodefApiService;
import org.scoula.security.util.JwtProcessor;
import org.scoula.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/account")
@Log4j2
@RequiredArgsConstructor
@Api(tags = "청약 계좌 API", description = "CODEF 기반 청약 통장 연동 및 조회")
public class AccountController {

    private final CodefApiService codefApiService;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

    @PostMapping("/connect")
    @ApiOperation(value = "청약 계좌 연동", notes = "CODEF API를 통해 사용자의 청약 계좌를 연동합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "계좌 등록 성공"),
            @ApiResponse(code = 400, message = "계좌 등록 실패")
    })
    public ResponseEntity<Map<String, String>> autoConnectAndFetchAccount(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String token,
            @RequestBody AccountConnectDTO requestDto
    ) {
        try {
            String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
            int userIdx = userMapper.findUserIdxByUserId(userId);

            codefApiService.autoConnectAndFetchChungyakAccount(
                    requestDto.getId(),
                    requestDto.getPassword(),
                    requestDto.getOrganization(),
                    requestDto.getBankName(),
                    userIdx
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "계좌 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("청약 계좌 연결 실패", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "계좌 등록에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }



    @GetMapping("")
    @ApiOperation(value = "청약 계좌 조회", notes = "사용자의 청약 계좌 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "계좌 조회 성공", response = ChungyakAccountDTO.class),
            @ApiResponse(code = 404, message = "등록된 계좌 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<ChungyakAccountDTO> getUserAccount(@ApiParam(hidden = true) @RequestHeader("Authorization") String token) {
        try {
            String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
            int userIdx = userMapper.findUserIdxByUserId(userId);

            ChungyakAccountDTO account = codefApiService.getAccountByUserIdx(userIdx);
            return ResponseEntity.ok(account);

        } catch (NoAccountException e) {
            log.warn("계좌 조회 실패 - 계좌 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("계좌 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("")
    @ApiOperation(value = "청약 계좌 삭제", notes = "사용자의 청약 계좌를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "계좌 삭제 성공"),
            @ApiResponse(code = 404, message = "등록된 계좌 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<Map<String, String>> deleteUserAccount(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String token
    ) {
        try {
            String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
            int userIdx = userMapper.findUserIdxByUserId(userId);

            boolean deleted = codefApiService.deleteAccountByUserIdx(userIdx);

            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "계좌가 성공적으로 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "삭제할 계좌가 존재하지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            log.error("계좌 삭제 실패", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "계좌 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
