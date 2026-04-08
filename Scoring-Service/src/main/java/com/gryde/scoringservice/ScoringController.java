package com.gryde.scoringservice;

import com.gryde.contract.ScoringRequest;
import com.gryde.contract.ScoringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/scoring")
public class ScoringController {

    private final ScoringService scoringService;

    @PostMapping("/calculate")
    public ScoringResponse internalScoring(
            @RequestBody ScoringRequest request
    ) {
        System.out.println("InternalScoring request: " + request);
        return scoringService.calculate(request);
    }
}
