package com.gryde.bureauservice.mapper;

import com.gryde.bureauservice.dto.CreditAccountDto;
import com.gryde.bureauservice.entity.CreditAccount;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditAccountMapper {

    CreditAccountDto toDto(CreditAccount entity);

    List<CreditAccountDto> toDtoList(List<CreditAccount> entities);
}
