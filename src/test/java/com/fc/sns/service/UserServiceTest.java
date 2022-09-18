package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.UserEntityFixture;
import com.fc.sns.model.entity.UserEntity;
import com.fc.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
//@TestPropertySource(properties = "jwt.secret-key=test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Spy
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password, 1L);
        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());
        given(encoder.encode(password)).willReturn("encrypt password");
        given(userEntityRepository.save(any(UserEntity.class))).willReturn(fixture);

        //when
        userService.join(userName, password);

        //then
        then(userEntityRepository).should().save(any(UserEntity.class));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password, 1L);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(fixture));
        given(userService.join(userName, password)).willThrow(SnsApplicationException.class);


        // when && then
        assertThatThrownBy(() -> userService.join(userName, password))
                .isInstanceOf(SnsApplicationException.class);

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_USER_NAME);

    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        //given
        String userName = "username";
        String password = "password";
        UserEntity userEntity = UserEntityFixture.get(userName, encoder.encode(password), 1l);

        given(userEntityRepository.findByUserName(userEntity.getUserName())).willReturn(Optional.of(userEntity));
        given(encoder.matches(password, userEntity.getPassword())).willReturn(true);

        //when
        userService.login(userEntity.getUserName(), password);

        //then
//        then(userEntityRepository).should().findByUserName(fixture.getUserName());
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, encoder.encode(password), 1l);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> userService.login(userName, password))
                .isInstanceOf(SnsApplicationException.class);

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        //given
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";
        UserEntity fixture = UserEntityFixture.get(userName, password, 1L);

        given(userEntityRepository.findByUserName(userName)).willReturn(Optional.of(fixture));

        // when && then
        assertThatThrownBy(() -> userService.login(userName, wrongPassword))
                .isInstanceOf(SnsApplicationException.class);

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

}
