package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.BureauSnapshot;
import com.gryde.applicationorchestrator.mapper.BureauSnapshotMapper;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.applicationorchestrator.repository.BureauSnapshotRepository;
import com.gryde.contract.BureauSnapshotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BureauSnapshotService {

    private final BureauSnapshotMapper mapper;
    private final BureauSnapshotRepository repository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public void saveSnapshot(UUID applicationId, BureauSnapshotResponse bureauResponse) {
        Application application = applicationRepository.findById(applicationId).
                orElseThrow(() -> new NoSuchElementException("Application with UUID: " + applicationId + " not found"));
        BureauSnapshot snapshot = mapper.toEntity(application, bureauResponse);
        snapshot.setApplication(application);
        application.setBureauSnapshot(snapshot);
        repository.save(snapshot);
    }
}
