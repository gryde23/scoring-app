package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.User;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import com.gryde.applicationorchestrator.repository.ApplicationRepository;
import com.gryde.applicationorchestrator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public ApplicationDTO createApplication(ApplicationCreateRequest request) {
        User user = userRepository.findById(request.userUUID()).
                orElseThrow(() -> new NoSuchElementException("User with UUID: " + request.userUUID() + " not found"));

        Application application = ApplicationMapper.toEntity(request, user);

        Application saved = applicationRepository.save(application);
        logger.info("Saved application with UUID: {}", saved.getId());
        return ApplicationMapper.toDTO(saved);
    }

    public ApplicationDTO findApplicationById(UUID uuid) {
        Application application = applicationRepository.findById(uuid).
                orElseThrow(() -> new NoSuchElementException("Application with UUID: " + uuid + " not found"));

        return ApplicationMapper.toDTO(application);
    }

    public List<ApplicationDTO> findUserApplicationsByPhoneOrEmail(String phone, String email) {
        List<Application> userApplications;

        if (phone != null) {
            userApplications = applicationRepository.findApplicationsByUserPhone(phone);
            logger.info("Found {} applications for user with phone {}", userApplications.size(), phone);
        } else {
            userApplications = applicationRepository.findApplicationsByUserEmail(email);
            logger.info("Found {} applications for user with email {}", userApplications.size(), email);
        }

        return userApplications.stream().map(ApplicationMapper::toDTO).toList();
    }
}
