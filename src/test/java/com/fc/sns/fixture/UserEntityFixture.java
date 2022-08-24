package com.fc.sns.fixture;

import com.fc.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password) {
        UserEntity result = UserEntity.of(userName, password);
        result.setId(1L);
        return result;
    }

}
