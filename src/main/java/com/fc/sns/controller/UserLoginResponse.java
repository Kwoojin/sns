package com.fc.sns.controller;

import com.fc.sns.model.User;
import com.fc.sns.model.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginResponse {

    private String token;

    public static UserLoginResponse of(String token) {
        return new UserLoginResponse(token);
    }
}
