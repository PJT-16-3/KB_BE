package org.scoula.controller;

import lombok.extern.log4j.Log4j2;
import org.scoula.security.dto.CustomUser;
import org.scoula.security.dto.MemberDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RequestMapping("/api/security")
@RestController
public class SecurityController {

    @GetMapping("/member")
    public ResponseEntity<String> doMember(Authentication authentication) {
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        log.info("username = " + userDetails.getUsername());
        return ResponseEntity.ok(userDetails.getUsername());
    }
    @GetMapping("/all")
    public ResponseEntity<String> doAll() {
        log.info("do all can access everybody");
        return ResponseEntity.ok("All can access everybody");
    }


    @GetMapping("/admin")
    public ResponseEntity<MemberDTO> doAdmin(@AuthenticationPrincipal CustomUser customUser) {
        MemberDTO member=customUser.getMember();
        log.info("username = " + member);
        return ResponseEntity.ok(member);
    }
}