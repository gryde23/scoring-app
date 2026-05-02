package com.gryde.applicationorchestrator.admin.dto;

import com.gryde.applicationorchestrator.dto.ApplicationShortResponse;
import com.gryde.contract.ApplicationResponse;
import com.gryde.contract.BureauSnapshotResponse;
import com.gryde.contract.DecisionDTO;

import java.util.List;

public record ApplicationFullReviewResponse(
        ApplicationResponse applicationResponse,
        BureauSnapshotResponse bureauSnapshotResponse,
        DecisionDTO decisionDTO,
        List<ApplicationShortResponse> userApplications
) {
}
