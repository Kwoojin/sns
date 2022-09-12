package com.fc.sns.service;

import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.entity.PostEntity;
import com.fc.sns.model.entity.UserEntity;
import com.fc.sns.repository.PostEntityRepository;
import com.fc.sns.repository.UserEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;


@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트작성이_성공한경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";

        given(userEntityRepository.findByUserName(userName)).willReturn(any(Optional.class));
        given(postEntityRepository.save(PostEntity.of(eq(title), eq(body), any(UserEntity.class)))).willReturn(any(PostEntity.class));

        postService.create(title, body, userName);

    }

    @Test
    void 포스트작성시_요청한유저가_존재하지않은경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());
//        given(postEntityRepository.save(PostEntity.of(any(String.class), any(String.class), any(UserEntity.class)))).willReturn(any(PostEntity.class));


        assertThatThrownBy(() -> postService.create(title, body, userName))
                .isInstanceOf(SnsApplicationException.class);
    }
}
