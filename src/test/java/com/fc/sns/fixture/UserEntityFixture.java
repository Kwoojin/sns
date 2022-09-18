package com.fc.sns.fixture;

import com.fc.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password, Long userId) {
        UserEntity result = UserEntity.of(userName, password);
        result.setId(userId);
        return result;
    }

}
