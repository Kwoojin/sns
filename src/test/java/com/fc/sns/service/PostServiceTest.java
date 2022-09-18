package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.PostEntityFixture;
import com.fc.sns.fixture.UserEntityFixture;
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

        given(userEntityRepository.findByUserName(eq(userName))).willReturn(Optional.of(mock(UserEntity.class)));
//        given(postEntityRepository.save(PostEntity.of(eq(title), eq(body), any(UserEntity.class)))).willReturn(any(PostEntity.class));
        given(postEntityRepository.save(any())).willReturn(mock(PostEntity.class));

        postService.create(title, body, userName);

    }

    @Test
    void 포스트작성시_요청한유저가_존재하지않은경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());
        given(postEntityRepository.save(any())).willReturn(mock(PostEntity.class));


        assertThatThrownBy(() -> postService.create(title, body, userName))
                .isInstanceOf(SnsApplicationException.class);

    }

    @Test
    void 포스트수정이_성공한경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1L);
        UserEntity userEntity = postEntity.getUser();

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(userEntity));
        given(postEntityRepository.findById(postId)).willReturn(Optional.of(postEntity));
        given(postEntityRepository.saveAndFlush(any())).willReturn(postEntity);

        postService.modify(title, body, userName, postId);
    }

    @Test
    void 포스트수정이_포스트가_존재하지않는_경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1L);
        UserEntity userEntity = postEntity.getUser();

        given(userEntityRepository.findByUserName(eq(userName))).willReturn(Optional.of(mock(UserEntity.class)));
        given(postEntityRepository.findById(postId)).willReturn(Optional.empty());

        SnsApplicationException e = (SnsApplicationException) catchThrowable(() -> postService.modify(title, body, userName, postId));
        Assertions.assertThat(e.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

        /*
        assertThatCode(() -> postService.modify(title, body, userName, postId))
                .matches(failed -> failed instanceof SnsApplicationException)
                .matches(throwable -> ((SnsApplicationException) throwable).getErrorCode() == ErrorCode.POST_NOT_FOUND);
        */
    }

    @Test
    void 포스트수정이_권한이_없는_경우() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1L);
        UserEntity writer = UserEntityFixture.get("userName1", "password1", 2L);


        given(userEntityRepository.findByUserName(eq(userName))).willReturn(Optional.of(writer));
        given(postEntityRepository.findById(postId)).willReturn(Optional.of(postEntity));

        SnsApplicationException e = (SnsApplicationException) catchThrowable(() -> postService.modify(title, body, userName, postId));
        Assertions.assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMISSION);
    }

}
