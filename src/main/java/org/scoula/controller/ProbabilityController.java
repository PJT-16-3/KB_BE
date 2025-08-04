package org.scoula.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.scoula.dto.ProbabilityDTO;
import org.scoula.service.ProbabilityService;
import org.scoula.util.TokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "❌❌미구현 ❌❌")
@RequestMapping("/api/probability")
public class ProbabilityController {

    private final ProbabilityService probabilityService;
    private final TokenUtils tokenUtils;

    @PostMapping("/calculate")
    public ResponseEntity<Double> calculate(@RequestBody ProbabilityDTO request,
                                            @RequestHeader("Authorization") String bearerToken) {
        String accessToken = tokenUtils.extractAccessToken(bearerToken);
        double probability = probabilityService.calculateProbability(request);
        return ResponseEntity.ok(probability);
    }

    @GetMapping("/calculate/{aptIdx}")
    public ResponseEntity<Double> calculateByAptIdx(@PathVariable int aptIdx,
                                                    @RequestParam double score,
                                                    @RequestHeader("Authorization") String bearerToken) {
        String accessToken = tokenUtils.extractAccessToken(bearerToken);
        double probability = probabilityService.calculateByAptIdx(aptIdx, score);
        return ResponseEntity.ok(probability);
    }
}
