package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.ActionType;
import com.gryde.applicationorchestrator.admin.dto.AdminUpdateApplicationRequest;
import com.gryde.applicationorchestrator.admin.dto.ApplicationFullReviewResponse;
import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
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
import com.gryde.contract.DecisionDTO;
import com.gryde.contract.enums.FinalDecision;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
        DecisionDTO decisionDTO = decisionMapper.toDto(application.getDecision());
        List<ApplicationShortResponse> userApplications = applicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(applicationMapper::toShortResponse)
                .toList();

        return new ApplicationFullReviewResponse(
                applicationResponse,
                bureauSnapshotResponse,
                decisionDTO,
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
}
