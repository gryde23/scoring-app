package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.dto.UserResponse;
import com.gryde.applicationorchestrator.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);
}
