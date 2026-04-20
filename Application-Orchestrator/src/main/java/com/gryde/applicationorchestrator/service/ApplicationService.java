package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.contract.ApplicationResponse;
import com.gryde.contract.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {


    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateRequest request, UUID userId) {

        Application application = applicationMapper.toEntity(request, userId);
        application.setStatus(ApplicationStatus.IN_PROGRESS);

        Application saved = applicationRepository.save(application);
        logger.info("Saved application with UUID: {}", saved.getId());
        return applicationMapper.toResponse(saved);
    }

    public ApplicationResponse findApplicationById(UUID uuid) {
        Application application = applicationRepository.findById(uuid).
                orElseThrow(() -> new NoSuchElementException("Application with UUID: " + uuid + " not found"));

        return applicationMapper.toResponse(application);
    }

    public List<ApplicationResponse> findCompletedApplicationsByUserIdForLastMonth(UUID userId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<Application> applications = applicationRepository.findApplicationsByUserIdForLastMonth(userId, startDate);

        return applications.stream().map(applicationMapper::toResponse).toList();
    }

    @Transactional
    public void updateStatus(UUID uuid, ApplicationStatus status) {
        Application application = applicationRepository.findById(uuid).
                orElseThrow(() -> new NoSuchElementException("Application with UUID: " + uuid + " not found"));

        application.setStatus(status);
        applicationRepository.save(application);
    }
}
