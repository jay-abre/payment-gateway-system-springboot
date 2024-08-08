package com.electric_titans.userservice.mapper;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User registerRequestToUser(RegisterRequest registerRequest);

    UserResponse userToUserResponse(User user);
}
