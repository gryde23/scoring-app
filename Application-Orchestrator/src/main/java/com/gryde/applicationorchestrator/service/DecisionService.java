package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.Decision;
import com.gryde.applicationorchestrator.dto.DecisionResult;
import com.gryde.applicationorchestrator.mapper.DecisionMapper;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.applicationorchestrator.repository.DecisionRepository;
import com.gryde.contract.AntifraudResponse;
import com.gryde.contract.DecisionDTO;
import com.gryde.contract.ScoringResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final ApplicationRepository applicationRepository;
    private final DecisionMapper decisionMapper;

    public List<DecisionDTO> findDecisionsByUserIdForLastMonth(UUID userId) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        return decisionMapper.toDtoList(decisionRepository.findDecisionsByUserIdForLastMonth(userId, startDate));
    }

    public DecisionDTO save(
            UUID applicationId,
            ScoringResponse scoring,
            Integer bureauScore,
            AntifraudResponse antifraud,
            DecisionResult decisionResult
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));

        Decision decision = new Decision();
        decision.setApplication(application);
        decision.setInternalScore(scoring.internalScore());
        decision.setMlDefaultProbability(scoring.mlDefaultProbability());
        decision.setBureauScore(bureauScore);
        decision.setAntifraudScore(antifraud.antifraudScore());
        decision.setAntifraudFlags(antifraud.antifraudFlags());
        decision.setFinalDecision(decisionResult.finalDecision());
        decision.setDecisionReasons(decisionResult.decisionReasons());
        decision.setApprovedLimit(decisionResult.approvedLimit());

        Decision saved = decisionRepository.save(decision);
        return decisionMapper.toDto(saved);
    }
}
