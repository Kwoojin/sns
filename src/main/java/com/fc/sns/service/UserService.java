package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.Alarm;
import com.fc.sns.model.User;
import com.fc.sns.model.entity.UserEntity;
import com.fc.sns.repository.AlarmEntityRepository;
import com.fc.sns.repository.UserEntityRepository;
import com.fc.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        return User.fromEntity(getUserEntityOrException(userName));
    }


    @Transactional
    public User join(String userName, String password) {
        // 회원가입 하려는 userName 으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));

        return User.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        UserEntity userEntity = getUserEntityOrException(userName);

        // 비밀번호 체크
        if(encoder.matches(password, userEntity.getPassword()) == false) {
            log.error("??");
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }

    // TODO : alarm return
    public Page<Alarm> alarmList(String userName, Pageable pageable) {
        UserEntity userEntity = getUserEntityOrException(userName);
        return alarmEntityRepository.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
    }

    private UserEntity getUserEntityOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not, founded", userName)));
    }

}
