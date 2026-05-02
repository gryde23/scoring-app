package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
import com.gryde.applicationorchestrator.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminApplicationMapper {

    @Mapping(target = "applicationId", source = "application.id")
    ManualReviewApplicationResponse toManualReviewResponse(Application application);
}
