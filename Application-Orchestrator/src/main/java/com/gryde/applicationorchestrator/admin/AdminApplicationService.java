package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.Decision;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.applicationorchestrator.repository.DecisionRepository;
import com.gryde.contract.enums.FinalDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AdminReviewActionRepository adminReviewActionRepository;
    private final DecisionRepository decisionRepository;
    private final AdminApplicationMapper adminApplicationMapper;

    public Page<ManualReviewApplicationResponse> getManualReviewApplications(Pageable pageable) {

        Page<Application> applicationPage = applicationRepository.findAllByDecision(FinalDecision.MANUAL_REVIEW, pageable);

        return applicationPage.map(adminApplicationMapper::toManualReviewResponse);
    }
}
