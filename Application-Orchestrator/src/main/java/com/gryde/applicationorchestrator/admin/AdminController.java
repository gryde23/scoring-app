package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/applications")
public class AdminController {

    private final AdminApplicationService applicationService;

    @GetMapping("/manual-review")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ManualReviewApplicationResponse> getManualReviewApplications(
           @PageableDefault(
                   size = 20,
                   sort = "createdAt",
                   direction = Sort.Direction.ASC
           ) Pageable pageable
    ) {
        return applicationService.getManualReviewApplications(pageable);
    }
}
