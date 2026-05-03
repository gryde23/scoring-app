package com.gryde.applicationorchestrator.admin;

import com.gryde.applicationorchestrator.admin.dto.AdminUpdateApplicationRequest;
import com.gryde.applicationorchestrator.admin.dto.ManualReviewApplicationResponse;
import com.gryde.applicationorchestrator.entity.Application;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminApplicationMapper {

    @Mapping(target = "applicationId", source = "application.id")
    ManualReviewApplicationResponse toManualReviewResponse(Application application);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateApplicationFromRequest(
            AdminUpdateApplicationRequest request,
            @MappingTarget Application application
    );
}
