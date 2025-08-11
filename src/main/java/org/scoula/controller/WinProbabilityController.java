package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.dto.AccountConnectDTO;
import org.scoula.dto.AptPredictRequestDTO;
import org.scoula.dto.PythonAptPredictRequestDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.service.PythonApiService;
import org.scoula.service.UserSelectedService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/winprobability")
public class WinProbabilityController {
    private final PythonApiService pythonApiService;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

//    @PostMapping("/apt")
//    public ResponseEntity<Double> getPrediction(@RequestBody @RequestHeader("Authorization") String bearerToken,
//                                                @RequestBody AptPredictRequestDTO request) {
//        String accessToken = tokenUtils.extractAccessToken(bearerToken);
//        String userId = jwtProcessor.getUsername(accessToken);
//        int userIdx = userMapper.findUserIdxByUserId(userId);
//
//    }
}