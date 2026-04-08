package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.mapper.ApplicationDecisionMapper;
import com.gryde.applicationorchestrator.repository.ApplicationDecisionRepository;
import com.gryde.contract.ApplicationDecisionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationDecisionService {

    private final ApplicationDecisionRepository repository;
    private final ApplicationDecisionMapper mapper;

    public List<ApplicationDecisionDTO> findDecisionsByUserIdForLastTwoMonth(UUID userId) {

        return mapper.toDtoList(repository.findDecisionsByUserIdForLastTwoMonth(userId));
    }
}
