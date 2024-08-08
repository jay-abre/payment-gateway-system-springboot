package com.electric_titans.userservice.dto.response;

import com.electric_titans.userservice.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private UserStatusEnum status;
}
