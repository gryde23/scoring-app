package com.gryde.bureauservice.controller;

import com.gryde.bureauservice.service.BureauService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bureau")
public class BureauController {

    private final BureauService bureauService;

    @PostMapping
    public ResponseEntity<String> setUserUUID(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "phone") String phone) {
        bureauService.setUserUUID(userId, phone);
        return ResponseEntity.ok("Set user UUID in BKI");
    }

    @GetMapping
    public ResponseEntity<Integer> calculateBureauScore(
            @RequestParam(name = "userId") UUID userId) {
        Integer score = bureauService.calculateBureauScore(userId);
        return ResponseEntity.ok(score);
    }
}
