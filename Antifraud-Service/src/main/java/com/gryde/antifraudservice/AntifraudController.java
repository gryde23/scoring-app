package com.gryde.antifraudservice;

import com.gryde.contract.AntifraudRequest;
import com.gryde.contract.AntifraudResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/antifraud")
public class AntifraudController {

    private final AntifraudService antifraudService;

    @PostMapping
    public ResponseEntity<AntifraudResponse> antifraudCheck(
            @RequestBody AntifraudRequest request
            ) {
        return ResponseEntity.ok(antifraudService.antifraudCheck(request));
    }
}
