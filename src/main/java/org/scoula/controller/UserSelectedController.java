package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.dto.UserSelectedDTO;
import org.scoula.service.UserSelectedService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user/preferences")
@RequiredArgsConstructor
public class UserSelectedController {

    private final UserSelectedService userSelectedService;
    private final JwtProcessor jwtProcessor;

    @PostMapping
    public void saveUserSelected(@RequestHeader("Authorization") String token,
                                 @RequestBody UserSelectedDTO userSelectedDTO) {
        String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
        userSelectedService.saveAllPreferences(userId, userSelectedDTO);
    }

    @GetMapping
    public UserSelectedDTO getUserSelected(@RequestHeader("Authorization") String token) {
        String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
        return userSelectedService.getUserSelected(userId);
    }
}