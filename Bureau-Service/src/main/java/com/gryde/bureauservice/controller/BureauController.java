package com.gryde.bureauservice.controller;

import com.gryde.bureauservice.service.BureauService;
import com.gryde.contract.AddAccountToBureauRequest;
import com.gryde.contract.BureauResultResponse;
import com.gryde.contract.BureauSnapshotResponse;
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
    public ResponseEntity<BureauResultResponse> calculateBureauScore(@RequestParam UUID userId) {
        if (bureauService.hasSelfBan(userId)) {
            return ResponseEntity.ok(
                    new BureauResultResponse(true, null)
            );
        }

        BureauSnapshotResponse response = bureauService.collectBureauData(userId);
        return ResponseEntity.ok(
                new BureauResultResponse(false, response)
        );
    }

    @PostMapping
    public ResponseEntity<Void> addAccount(
            @RequestBody AddAccountToBureauRequest request
    ) {
        bureauService.addAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
