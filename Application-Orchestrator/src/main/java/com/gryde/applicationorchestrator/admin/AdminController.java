package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.AdminUpdateApplicationRequest;
import com.gryde.applicationorchestrator.admin.dto.ApplicationFullReviewResponse;
import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
import com.gryde.applicationorchestrator.dto.ApplicationShortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/applications")
public class AdminController {

    private final AdminApplicationService applicationService;

    @GetMapping("/manual-review")
    public Page<ManualReviewApplicationResponse> getManualReviewApplications(
           @PageableDefault(
                   size = 20,
                   sort = "createdAt",
                   direction = Sort.Direction.ASC
           ) Pageable pageable
    ) {
        return applicationService.getManualReviewApplications(pageable);
    }

    @GetMapping("/{applicationId}")
    public ApplicationFullReviewResponse getApplicationFullInfo(
            @PathVariable UUID applicationId
    ) {
        return applicationService.getApplicationFullInfo(applicationId);
    }

    @GetMapping("/users/{userId}")
    public List<ApplicationShortResponse> getUserApplications(
            @PathVariable UUID userId
    ) {
        return applicationService.getUserApplications(userId);
    }

    @PatchMapping("/{applicationId}")
    public ApplicationFullReviewResponse updateApplication(
            @PathVariable UUID applicationId,
            @RequestBody AdminUpdateApplicationRequest updateApplicationRequest,
            Authentication authentication
    ) {
        UUID employeeId = (UUID) authentication.getPrincipal();

        return applicationService.updateApplication(applicationId, updateApplicationRequest, employeeId);
    }
}
