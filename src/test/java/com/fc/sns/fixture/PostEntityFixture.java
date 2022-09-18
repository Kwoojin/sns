package com.fc.sns.fixture;

import com.fc.sns.model.entity.PostEntity;
import com.fc.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Long postId, Long userId) {
        UserEntity user = UserEntity.builder()
                .id(userId)
                .userName(userName)
                .build();

        PostEntity result = PostEntity.builder()
                .user(user)
                .build();
        result.setId(postId);
        return null;
    }

}
