package com.gryde.bureauservice.mapper;

import com.gryde.bureauservice.dto.CreditAccountDto;
import com.gryde.bureauservice.entity.CreditAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditAccountMapper {

    CreditAccountDto toDto(CreditAccount entity);

    CreditAccount toEntity(CreditAccountDto dto);
}
