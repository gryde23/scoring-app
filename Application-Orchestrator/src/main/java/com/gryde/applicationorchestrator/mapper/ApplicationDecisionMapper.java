package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.entity.ApplicationDecision;
import com.gryde.contract.ApplicationDecisionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationDecisionMapper {

    @Mapping(target = "application", ignore = true)
    ApplicationDecisionDTO toDto(ApplicationDecision entity);

    List<ApplicationDecisionDTO> toDtoList(List<ApplicationDecision> entities);
}
