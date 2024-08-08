package com.electric_titans.userservice.mapper;

import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.dto.response.UserProfileResponse;
import com.electric_titans.userservice.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    UserProfile userProfileRequestToUserProfile(UserProfileRequest userProfileRequest);

    UserProfileResponse userProfileToUserProfileResponse(UserProfile userProfile);
}
