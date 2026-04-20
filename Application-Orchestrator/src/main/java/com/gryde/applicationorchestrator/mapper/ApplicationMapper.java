package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.contract.ApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userUUID", source = "userId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)

    @Mapping(target = "decision", ignore = true)
    @Mapping(target = "bureauSnapshot", ignore = true)
    Application toEntity(ApplicationCreateRequest request, UUID userId);

    @Mapping(target = "userId", source = "userUUID")
    ApplicationResponse toResponse(Application application);
}
