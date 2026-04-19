package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.service.ApplicationService;
import com.gryde.contract.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

}
