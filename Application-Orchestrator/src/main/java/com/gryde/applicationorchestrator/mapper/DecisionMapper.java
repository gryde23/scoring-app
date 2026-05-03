package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.entity.Decision;
import com.gryde.contract.DecisionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DecisionMapper {

    @Mapping(target = "id", source = "application.id")
    DecisionResponse toDto(Decision entity);

    List<DecisionResponse> toDtoList(List<Decision> entities);
}
