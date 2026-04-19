package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.BureauSnapshot;
import com.gryde.contract.BureauSnapshotResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BureauSnapshotMapper {

    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "application", source = "application")
    @Mapping(target = "createdAt", ignore = true)
    BureauSnapshot toEntity(Application application, BureauSnapshotResponse dto);
}