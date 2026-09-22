package com.hab.emmaus.app_user.event;

import lombok.Builder;

@Builder
public record AdminSetPasswordEvent(
        String userName,
        String email,
        String password,
        String reason
){
}
