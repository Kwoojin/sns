package com.fc.sns.service;

import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.User;
import com.fc.sns.model.entity.UserEntity;
import com.fc.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO : implement
    public User join(String username, String password) {
        // 회원가입 하려는 userName 으로 회원가입된 user가 있는지
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(username);

        // 회원가입 진행 = user 등록
        userEntityRepository.save(new UserEntity());
        
        return new User(username, password);
    }

    // TODO : implement
    public String login(String username, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(username).orElseThrow(() -> new SnsApplicationException());

        // 비밀번호 체크
        if (userEntity.getUserName().equals(password)){
            throw new SnsApplicationException();
        }

        // 토큰 생성


        return "";
    }
}
