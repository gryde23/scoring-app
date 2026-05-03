package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.*;
import com.gryde.applicationorchestrator.dto.ApplicationShortResponse;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.Decision;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import com.gryde.applicationorchestrator.mapper.BureauSnapshotMapper;
import com.gryde.applicationorchestrator.mapper.DecisionMapper;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.applicationorchestrator.repository.DecisionRepository;
import com.gryde.contract.ApplicationResponse;
import com.gryde.contract.BureauSnapshotResponse;
import com.gryde.contract.DecisionResponse;
import com.gryde.contract.enums.ApplicationStatus;
import com.gryde.contract.enums.FinalDecision;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AdminReviewActionRepository adminReviewActionRepository;
    private final DecisionRepository decisionRepository;
    private final AdminApplicationMapper adminApplicationMapper;
    private final ApplicationMapper applicationMapper;
    private final BureauSnapshotMapper bureauSnapshotMapper;
    private final DecisionMapper decisionMapper;

    @Transactional(readOnly = true)
    public Page<ManualReviewApplicationResponse> getManualReviewApplications(Pageable pageable) {

        Page<Application> applicationPage = applicationRepository.findAllByDecision(FinalDecision.MANUAL_REVIEW, pageable);

        return applicationPage.map(adminApplicationMapper::toManualReviewResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationFullReviewResponse getApplicationFullInfo(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        UUID userId = application.getUserUUID();

        ApplicationResponse applicationResponse = applicationMapper.toResponse(application);
        BureauSnapshotResponse bureauSnapshotResponse = bureauSnapshotMapper.toResponse(application.getBureauSnapshot());
        DecisionResponse decisionResponse = decisionMapper.toDto(application.getDecision());
        List<ApplicationShortResponse> userApplications = applicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(applicationMapper::toShortResponse)
                .toList();

        return new ApplicationFullReviewResponse(
                applicationResponse,
                bureauSnapshotResponse,
                decisionResponse,
                userApplications
        );
    }

    public List<ApplicationShortResponse> getUserApplications(UUID userId) {
        return applicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(applicationMapper::toShortResponse)
                .toList();
    }

    @Transactional
    public ApplicationFullReviewResponse updateApplication(
            UUID applicationId,
            AdminUpdateApplicationRequest request,
            UUID employeeId
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        if (!application.getDecision().getFinalDecision().equals(FinalDecision.MANUAL_REVIEW)) {
            throw new IllegalStateException("Редактировать можно только заявку в статусе MANUAL_REVIEW");
        }

        String oldValue = application.toString();

        adminApplicationMapper.updateApplicationFromRequest(request, application);

        String newValue = application.toString();

        AdminReviewAction action = new AdminReviewAction();
        action.setApplication(application);
        action.setActionType(ActionType.UPDATED_APPLICATION);
        action.setEmployeeId(employeeId);
        action.setOldValue(oldValue);
        action.setNewValue(newValue);
        action.setComment(request.comment());

        adminReviewActionRepository.save(action);

        return getApplicationFullInfo(applicationId);
    }

    @Transactional
    public DecisionResponse makeDecision(
            UUID applicationId,
            ManualDecisionRequest request,
            UUID employeeId
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        if (!application.getDecision().getFinalDecision().equals(FinalDecision.MANUAL_REVIEW)) {
            throw new IllegalStateException("Решение можно вынести только по заявке в статусе MANUAL_REVIEW");
        }

        if (request.decision() == ManualDecision.APPROVED && request.approvedLimit() == null) {
            throw new IllegalArgumentException("Для одобрения нужно указать кредитный лимит");
        }

        if (request.decision() == ManualDecision.REJECTED && request.reason().isBlank()) {
            throw new IllegalArgumentException("Для отказа нужно указать причину");
        }

        Decision decision = application.getDecision();
        String oldDecision = decisionMapper.toDto(decision).toString();

        if (request.decision().equals(ManualDecision.APPROVED)) {
            decision.setFinalDecision(FinalDecision.APPROVED);
            decision.setApprovedLimit(request.approvedLimit());
            decision.setDecisionReasons(List.of(request.reason()));
        } else {
            decision.setFinalDecision(FinalDecision.REJECTED);
            decision.setApprovedLimit(0);
            decision.setDecisionReasons(List.of(request.reason()));
        }

        String newDecision = decisionMapper.toDto(decision).toString();

        AdminReviewAction action = new AdminReviewAction();
        action.setApplication(application);
        action.setEmployeeId(employeeId);
        action.setActionType(request.decision() == ManualDecision.APPROVED
                ? ActionType.APPROVED_MANUALLY
                : ActionType.REJECTED_MANUALLY);
        action.setOldValue(oldDecision);
        action.setNewValue(newDecision);
        action.setComment(request.comment());

        adminReviewActionRepository.save(action);

        return decisionMapper.toDto(decision);
    }
}
