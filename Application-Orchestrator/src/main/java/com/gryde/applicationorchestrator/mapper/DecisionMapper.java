package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.entity.Decision;
import com.gryde.contract.DecisionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DecisionMapper {

    @Mapping(target = "id", source = "application.id")
    DecisionDTO toDto(Decision entity);

    List<DecisionDTO> toDtoList(List<Decision> entities);
}
