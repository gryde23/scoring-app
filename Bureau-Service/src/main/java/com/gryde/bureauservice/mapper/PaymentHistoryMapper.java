package com.gryde.bureauservice.mapper;

import com.gryde.bureauservice.dto.PaymentHistoryDto;
import com.gryde.bureauservice.entity.PaymentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentHistoryMapper {

    @Mapping(target = "accountId", source = "account.id")
    PaymentHistoryDto toDto(PaymentHistory entity);

    @Mapping(target = "account", ignore = true)
    PaymentHistory toEntity(PaymentHistoryDto dto);

    List<PaymentHistoryDto> toDtoList(List<PaymentHistory> entities);
}
