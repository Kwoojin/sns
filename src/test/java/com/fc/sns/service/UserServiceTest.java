package com.fc.sns.service;

import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.UserEntityFixture;
import com.fc.sns.model.entity.UserEntity;
import com.fc.sns.repository.UserEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);
        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());
        given(userEntityRepository.save(any(UserEntity.class))).willReturn(any(UserEntity.class));

        //when
        userService.join(userName, password);

        //then
        then(userEntityRepository).should().save(any(UserEntity.class));
    }

    @Test
    void 회원가입이_userName으로_회원가입한_유저가_이미_있는경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(fixture));
        given(userService.join(userName, password)).willThrow(SnsApplicationException.class);

        // when && then
        assertThatThrownBy(() -> userService.join(userName, password))
                .isInstanceOf(SnsApplicationException.class);
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(fixture));

        //when
        userService.login(userName, password);

        //then
        then(userEntityRepository).should().findByUserName(userName);
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> userService.login(userName, password))
                .isInstanceOf(SnsApplicationException.class);
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        //given
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(fixture));

        // when && then
        assertThatThrownBy(() -> userService.login(userName, wrongPassword))
                .isInstanceOf(SnsApplicationException.class);
    }
}