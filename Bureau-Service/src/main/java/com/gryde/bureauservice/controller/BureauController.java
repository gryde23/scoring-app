package com.gryde.bureauservice.controller;

import com.gryde.bureauservice.service.BureauService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bureau")
public class BureauController {

    private final BureauService bureauService;

    @GetMapping
    public ResponseEntity<Integer> calculateBureauScore(
            @RequestParam(name = "userId") UUID userId) {
        if (bureauService.hasSelfBan(userId)) {
            return ResponseEntity.ok(-1);
        }
        Integer score = bureauService.calculateBureauScore(userId);
        return ResponseEntity.ok(score);
    }
}
